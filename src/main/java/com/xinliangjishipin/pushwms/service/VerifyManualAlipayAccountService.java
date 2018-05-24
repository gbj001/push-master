package com.xinliangjishipin.pushwms.service;

import com.xinliangjishipin.pushwms.entity.AlipayBillExtend;
import com.xinliangjishipin.pushwms.entity.AlipayBillInfo;
import com.xinliangjishipin.pushwms.entity.CustomerInfo;
import com.xinliangjishipin.pushwms.mapper.AlipayBillExtendMapper;
import com.xinliangjishipin.pushwms.mapper.AlipayBillInfoMapper;
import com.xinliangjishipin.pushwms.mapper.CustomerInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VerifyManualAlipayAccountService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AlipayBillInfoMapper alipayBillInfoMapper;

    @Autowired
    private AlipayBillExtendMapper alipayBillExtendMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;


    public void verifyManualAlipayAccount()

    {
        //查询出所有需要（待校验和校验失败）的人工录入的支付宝相关
        List<AlipayBillExtend> unVerifyManualAlipayExtendList = alipayBillExtendMapper.unVerifyManualAlipayAccountList();
        for (AlipayBillExtend manualAlipayBillExtend : unVerifyManualAlipayExtendList) {
            AlipayBillInfo alipayBillInfo = alipayBillInfoMapper.getByBusinessNo(manualAlipayBillExtend.getBusinessNo());
            if(alipayBillInfo == null){
                manualAlipayBillExtend.setVerifyStatus("2");
                manualAlipayBillExtend.setVerifyTime(new Date());
                manualAlipayBillExtend.setVerifyText("系统下载的账单中没有找到业务流水号为：" + manualAlipayBillExtend.getBusinessNo() + " 的账单信息");
            }
            else {
                if(!this.compareManualAndAuto(manualAlipayBillExtend, alipayBillInfo)){
                    updateVerifyManualAlipayAccount(manualAlipayBillExtend, alipayBillInfo);
                }else{
                    manualAlipayBillExtend.setVerifyStatus("1");
                    manualAlipayBillExtend.setVerifyTime(new Date());
                    manualAlipayBillExtend.setVerifyText("和系统下载账单一致");
                }
            }
            alipayBillExtendMapper.updateMatchAlipayBillExtend(manualAlipayBillExtend);
        }
    }

    public boolean compareManualAndAuto(AlipayBillExtend manualAlipayBillExtend, AlipayBillInfo alipayBillInfo) {
        String manualAlipayBillSourceName = manualAlipayBillExtend.getSourceName();
        String manualAlipayBillSourceAccount = manualAlipayBillExtend.getSourceAccount();
        String manualAlipayBillIncomeAmount = manualAlipayBillExtend.getIncomeAmount();
        String alipayBillInfoSourceName = alipayBillInfo.getSourceName();
        String alipayBillInfoSourceAccount = alipayBillInfo.getSourceAccount();
        String alipayBillInfoIncomeAmount = alipayBillInfo.getIncomeAmount();
        //比较支付宝名称,手动输入如果是全名则截取从第2位开始的名字，若是包含*号，则删除*号后比较
        boolean flagSourceName = alipayBillInfoSourceName.replace("*", "").equals(manualAlipayBillSourceName.contains("*") ? manualAlipayBillSourceName.replace("*", "") : manualAlipayBillSourceName.substring(1, manualAlipayBillSourceName.length())) ? true : false;
        //比较支付宝账号
        boolean flagSourceAccount = false;
        String alipayAccountPrefix = alipayBillInfoSourceAccount.substring(0, 3);
        String manualAlipayAccountPrefix = manualAlipayBillSourceAccount.substring(0, 3);
        if(alipayBillInfoSourceAccount.contains("@")){
            String alipayAccountSuffix = alipayBillInfoSourceAccount.substring(alipayBillInfoSourceAccount.indexOf("@"), alipayBillInfoSourceAccount.length());
            String manualAlipayAccountSuffix = manualAlipayBillSourceAccount.substring(manualAlipayBillSourceAccount.indexOf("@"), manualAlipayBillSourceAccount.length());
            flagSourceAccount = alipayAccountPrefix.equals(manualAlipayAccountPrefix) && alipayAccountSuffix.equals(manualAlipayAccountSuffix);
        }
        else{
            String alipayAccountSuffix = alipayBillInfoSourceAccount.substring(alipayBillInfoSourceAccount.length() - 2, alipayBillInfoSourceAccount.length());
            String manualAlipayAccountSuffix = manualAlipayBillSourceAccount.substring(manualAlipayBillSourceAccount.length() - 2, manualAlipayBillSourceAccount.length());
            flagSourceAccount = alipayAccountPrefix.equals(manualAlipayAccountPrefix) && alipayAccountSuffix.equals(manualAlipayAccountSuffix);
        }
        boolean flagAmount = alipayBillInfoIncomeAmount.equals(manualAlipayBillIncomeAmount);

        return flagSourceName && flagSourceAccount && flagAmount;
    }


    public void updateVerifyManualAlipayAccount(AlipayBillExtend manualAlipayBillExtend, AlipayBillInfo alipayBillInfo){
        //更新手工录入的信息为不匹配状态，需要人工介入
        manualAlipayBillExtend.setUpdatedUser("system");
        manualAlipayBillExtend.setUpdatedTime(new Date());
        manualAlipayBillExtend.setVerifyStatus("0");
        manualAlipayBillExtend.setVerifyTime(new Date());
        String content = "系统校验失败：系统下载此账单信息为：{" + alipayBillInfo.getBusinessNo() + "--" + alipayBillInfo.getSourceName() + "--" + alipayBillInfo.getSourceAccount() + "--" + alipayBillInfo.getIncomeAmount() + "} \n"
                + " 手工录入信息为：{" + manualAlipayBillExtend.getBusinessNo() + "--" + manualAlipayBillExtend.getSourceName() + "--" + manualAlipayBillExtend.getSourceAccount() + "--" + manualAlipayBillExtend.getIncomeAmount() + "}";
        manualAlipayBillExtend.setVerifyText(content);
        alipayBillExtendMapper.updateMatchAlipayBillExtend(manualAlipayBillExtend);
        logger.info("系统下载与手工录入不匹配，具体信息为：" + content);
    }


}
