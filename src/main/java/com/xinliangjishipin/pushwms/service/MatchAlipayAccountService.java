package com.xinliangjishipin.pushwms.service;

import com.xinliangjishipin.pushwms.entity.AlipayBillExtend;
import com.xinliangjishipin.pushwms.entity.CustomerInfo;
import com.xinliangjishipin.pushwms.mapper.AlipayBillExtendMapper;
import com.xinliangjishipin.pushwms.mapper.AlipayBillInfoMapper;
import com.xinliangjishipin.pushwms.mapper.CustomerInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchAlipayAccountService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AlipayBillInfoMapper alipayBillInfoMapper;

    @Autowired
    private AlipayBillExtendMapper alipayBillExtendMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;


    public void matchAlipayAccount()

    {
        //查询出所有客户的信息放入list中
        List<AlipayBillExtend> alipayBillExtendList = alipayBillExtendMapper.unMatchList();
        for (AlipayBillExtend alipayBillExtend : alipayBillExtendList) {
            String alipaySourceNameSuffix = alipayBillExtend.getSourceName().replace("*", "");
            //系统导入
            //支付宝账号为邮箱的
            List<CustomerInfo> currentCustomerInfoList = new ArrayList<>();
            if (alipayBillExtend.getSourceAccount().contains("@")) {
                currentCustomerInfoList = accountIncludeEmail(alipayBillExtend.getSourceAccount());
            }
            //支付宝账号为手机号的
            else {
                currentCustomerInfoList = accountNotIncludeEmail(alipayBillExtend.getSourceAccount());
            }
            matchCustomer(currentCustomerInfoList, alipayBillExtend, alipaySourceNameSuffix);
        }
    }

    private void matchCustomer(List<CustomerInfo> currentCustomerInfoList, AlipayBillExtend alipayBillExtend, String alipaySourceNameSuffix){
        //1. 没有找到
        if (currentCustomerInfoList.size() == 0) {
            alipayBillExtend.setMatchStatus("0");
            //alipayBillExtend.setMatchContent("没有找到对应的账号信息:{account:" + alipayBillExtend.getSourceAccount() + ", accountName:" + alipayBillExtend.getSourceName() + "}");
            alipayBillExtend.setMatchContent("没有找到对应的账号信息");
        } else if (currentCustomerInfoList.size() == 1) {
            //两个支付宝账号匹配任意一个成功即为成功
            boolean isMatchName = (currentCustomerInfoList.get(0).getDefName1().contains(alipaySourceNameSuffix))
                    || (currentCustomerInfoList.get(0).getDefName2().contains(alipaySourceNameSuffix))
                    || (currentCustomerInfoList.get(0).getDefName3().contains(alipaySourceNameSuffix))
                    || (currentCustomerInfoList.get(0).getDefName4().contains(alipaySourceNameSuffix))
                    || (currentCustomerInfoList.get(0).getDefName5().contains(alipaySourceNameSuffix));
            alipayBillExtend.setMatchStatus(isMatchName ? "1" : "0");
            alipayBillExtend.setMatchCustomerCode(isMatchName ? currentCustomerInfoList.get(0).getCustomerCode() : "");
            alipayBillExtend.setMatchCustomerName(isMatchName ? currentCustomerInfoList.get(0).getCustomerName() : "");
            alipayBillExtend.setMatchContent(isMatchName ? "匹配成功:" + this.matchContent(currentCustomerInfoList) : "找到的信息:{account:" + alipayBillExtend.getSourceAccount() + ", name:" + alipayBillExtend.getSourceName() + "} 和对应的账号信息不一致:" + this.matchContent(currentCustomerInfoList));

        } else if (currentCustomerInfoList.size() > 1) {
            int count = 0;
            String customerCode = "";
            String customerName = "";
            for (CustomerInfo customerInfo : currentCustomerInfoList) {
                if (customerInfo.getDefName1().contains(alipaySourceNameSuffix)
                        || customerInfo.getDefName2().contains(alipaySourceNameSuffix)
                        || customerInfo.getDefName3().contains(alipaySourceNameSuffix)
                        || customerInfo.getDefName4().contains(alipaySourceNameSuffix)
                        || customerInfo.getDefName5().contains(alipaySourceNameSuffix)) {
                    count++;
                    customerCode = customerInfo.getCustomerCode();
                    customerName = customerInfo.getCustomerName();
                }
            }
            if (count == 0) {
                alipayBillExtend.setMatchStatus("0");
                //alipayBillExtend.setMatchContent("匹配失败：没有找到{account:" + alipayBillExtend.getSourceAccount() + ", name:" + alipayBillExtend.getSourceName() + "} 对应的账号信息:" + this.matchContent(currentCustomerInfoList));
                alipayBillExtend.setMatchContent("匹配失败：没有找到对应的账号信息");
            }
            if (count == 1) {
                alipayBillExtend.setMatchStatus("1");
                alipayBillExtend.setMatchCustomerCode(customerCode);
                alipayBillExtend.setMatchCustomerName(customerName);
                alipayBillExtend.setMatchContent("匹配成功：{account:" + alipayBillExtend.getSourceAccount() + ", name:" + alipayBillExtend.getSourceName() + "} 账号信息:" + this.matchContent(currentCustomerInfoList));
            }
            if (count > 1) {
                alipayBillExtend.setMatchStatus("0");
                alipayBillExtend.setMatchContent("匹配失败：找到{account:" + alipayBillExtend.getSourceAccount() + ", name:" + alipayBillExtend.getSourceName() + "} 多条对应的账号信息:" + this.matchContent(currentCustomerInfoList));
                alipayBillExtend.setDef1(this.matchContent(currentCustomerInfoList));
            }
        }
        alipayBillExtendMapper.updateMatchAlipayBillExtend(alipayBillExtend);
    }


    private String matchContent(List<CustomerInfo> customerInfos) {
        StringBuilder strbuf = new StringBuilder();
        for (CustomerInfo customerInfo : customerInfos) {
            strbuf.append("customer_code:").append(customerInfo.getCustomerCode());
            strbuf.append("customer_name:").append(customerInfo.getCustomerName());
            strbuf.append("|");
        }
        return strbuf.toString();

    }

    public List<CustomerInfo> accountIncludeEmail(String alipayAccount) {
        List<CustomerInfo> customerInfoList = new ArrayList<>();
        if(alipayAccount.contains("*") && alipayAccount.contains("@")){
            String alipayAccountPrefix = alipayAccount.substring(0, alipayAccount.indexOf("*"));
            String alipayAccountSuffix = alipayAccount.substring(alipayAccount.indexOf("@"), alipayAccount.length());
            customerInfoList = customerInfoMapper.getByEmailDefAccount(alipayAccountPrefix, alipayAccountSuffix);
        }
        else{
            customerInfoList = customerInfoMapper.getByFullAlipayAccount(alipayAccount);
        }
        return customerInfoList;
    }

    public List<CustomerInfo> accountNotIncludeEmail(String alipayAccount) {
        List<CustomerInfo> customerInfoList;
        if(alipayAccount.contains("*")){
            String alipayAccountPrefix = alipayAccount.substring(0, alipayAccount.indexOf("*"));
            String alipayAccountSuffix = alipayAccount.substring(alipayAccount.length() - 4, alipayAccount.length());
            customerInfoList = customerInfoMapper.getByDefAccount(alipayAccountPrefix, alipayAccountSuffix);
        }
        else{
            customerInfoList = customerInfoMapper.getByFullAlipayAccount(alipayAccount);
        }
        return customerInfoList;
    }
}
