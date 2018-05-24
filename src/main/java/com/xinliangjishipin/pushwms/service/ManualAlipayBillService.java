package com.xinliangjishipin.pushwms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinliangjishipin.pushwms.dto.CustomerInfoDto;
import com.xinliangjishipin.pushwms.entity.AlipayBillExtend;
import com.xinliangjishipin.pushwms.entity.CustomerInfo;
import com.xinliangjishipin.pushwms.entity.ResultResponse;
import com.xinliangjishipin.pushwms.mapper.AlipayBillExtendMapper;
import com.xinliangjishipin.pushwms.mapper.CustomerInfoMapper;
import com.xinliangjishipin.pushwms.utils.DateUtil;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ManualAlipayBillService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AlipayBillExtendMapper alipayBillExtendMapper;

    @Autowired
    private MatchAlipayAccountService matchAlipayAccountService;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;


    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);

    public PageInfo<AlipayBillExtend> manualAlipayAccount(String businessNo, String startTime, String endTime, String importType, String matchStatus, String verifyStatus,
                                                          String responseStatus, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<AlipayBillExtend> alipayBillExtendList = alipayBillExtendMapper.AllAlipayBillList(businessNo, startTime, endTime,
                importType, matchStatus, verifyStatus, responseStatus);
        PageInfo<AlipayBillExtend> a = new PageInfo<>(alipayBillExtendList);
        return new PageInfo<>(a.getList());
    }

    public void insertAlipayBillExtend(AlipayBillExtend alipayBillExtend) {
        alipayBillExtendMapper.insertAlipayBillExtend(alipayBillExtend);
    }

    public AlipayBillExtend getByBusinessNo(String businessNo) {
        return alipayBillExtendMapper.getByBusinessNo(businessNo);
    }


    @Transactional
    public ResultResponse matchResult(AlipayBillExtend alipayBillExtend) {
        //校验是流水编号并校验开头4位是否为当年年份
        String inputBusinussNo = alipayBillExtend.getBusinessNo();
        String currentDay = String.valueOf(LocalDate.now()).replaceAll("-", "");

        /*
        String currentYear = String.valueOf(LocalDate.now().getYear());
        if (!inputBusinussNo.startsWith(currentYear)) {
            return new ResultResponse("401", "业务流水号开头前4位必须为当前年份！");
        }


        if (Integer.parseInt(inputBusinussNo.substring(4, 6)) > 12 || Integer.parseInt(inputBusinussNo.substring(6, 8)) > 31) {
            return new ResultResponse("401", "业务流水号前8位中的月份大于12或者日期大于31，请检查前8位！");
        }
        */

        if (!inputBusinussNo.substring(0, 8).equals(currentDay)) {
            return new ResultResponse("401", "业务流水号前8位必须为当前日期！");
        }

        Date tradeDate = DateUtil.strToDate(inputBusinussNo.substring(0, 4) + "-" + inputBusinussNo.substring(4, 6) + "-" + inputBusinussNo.substring(6, 8), "yyyy-MM-dd");

        AlipayBillExtend alipayBillExtend1 = this.getByBusinessNo(alipayBillExtend.getBusinessNo());
        if (alipayBillExtend1 != null) {
            logger.info("账单流水号:" + alipayBillExtend.getBusinessNo() + " 已存在");
            return new ResultResponse("401", "账单流水号已经存在！");
        }

        ResultResponse resultResponse = new ResultResponse();
        //匹配数据
        List<CustomerInfo> customerInfoList = new ArrayList<>();
        if (alipayBillExtend.getSourceAccount().contains("@")) {
            customerInfoList = matchAlipayAccountService.accountIncludeEmail(alipayBillExtend.getSourceAccount());

        } else {
            customerInfoList = matchAlipayAccountService.accountNotIncludeEmail(alipayBillExtend.getSourceAccount());
        }

        //插入数据(无论是否匹配到客户，都先插入数据)
        AlipayBillExtend insertAlipayBillExtend = new AlipayBillExtend(
                alipayBillExtend.getBusinessNo(),
                tradeDate,
                alipayBillExtend.getSourceName(),
                alipayBillExtend.getSourceAccount(),
                alipayBillExtend.getIncomeAmount(),
                alipayBillExtend.getCreatedUser(),
                "M"
        );
        this.insertAlipayBillExtend(insertAlipayBillExtend);

        if (customerInfoList.size() == 0) {
            logger.info("没有找到:" + alipayBillExtend.getSourceAccount() + " 对应的NC客户信息");
            resultResponse.setResultCode("402");
            resultResponse.setResultMessage("没有找到(" + alipayBillExtend.getSourceAccount() + ")对应的NC客户信息,请尽快到NC系统维护");
        }
        if (customerInfoList.size() == 1) {

            //更新客户状态
            AlipayBillExtend updateAlipayBillExtend = new AlipayBillExtend();
            updateAlipayBillExtend.setBusinessNo(alipayBillExtend.getBusinessNo());
            updateAlipayBillExtend.setMatchStatus("1");
            updateAlipayBillExtend.setMatchContent("匹配成功");
            updateAlipayBillExtend.setMatchCustomerCode(customerInfoList.get(0).getCustomerCode());
            updateAlipayBillExtend.setMatchCustomerName(customerInfoList.get(0).getCustomerName());
            alipayBillExtendMapper.updateMatchAlipayBillExtend(updateAlipayBillExtend);
            resultResponse.setResultCode("200");
            resultResponse.setResultMessage("支付宝信息添加成功");
        }
        if (customerInfoList.size() > 1) {
            logger.info("找到多条(" + alipayBillExtend.getSourceAccount() + ")对应的NC客户信息");
            StringBuilder NCCumtomerInfo = new StringBuilder();
            for (CustomerInfo customerInfo : customerInfoList) {
                if (!customerInfo.getDefName1().equals("~") && !customerInfo.getDefAccount1().equals("~")) {
                    NCCumtomerInfo.append(customerInfo.getDefName1()).append("--").append(customerInfo.getDefAccount1());
                }
                if (!customerInfo.getDefName2().equals("~") && !customerInfo.getDefAccount2().equals("~")) {
                    NCCumtomerInfo.append(customerInfo.getDefName2()).append("--").append(customerInfo.getDefAccount2());
                }
            }
            resultResponse.setResultCode("402");
            resultResponse.setResultMessage("找到多条(" + alipayBillExtend.getSourceAccount() + ")对应的NC客户信息 \n " + NCCumtomerInfo.toString());
        }
        return resultResponse;
    }


    public List<CustomerInfoDto> convertStringToCustomerInfo(String customer) {
        List<CustomerInfoDto> customerInfoDtoList = new ArrayList<>();
        String[] customerArray = customer.split("\\|");
        for (String customerArr : customerArray) {
            CustomerInfoDto customerInfoDto = new CustomerInfoDto();
            customerInfoDto.setCustomerCode(customerArr.substring(customerArr.indexOf("_code:") + 6 , customerArr.indexOf("customer_name:")));
            customerInfoDto.setCustomerName(customerArr.substring(customerArr.indexOf("_name:") + 6, customerArr.length()));
            customerInfoDtoList.add(customerInfoDto);
        }
        return customerInfoDtoList;
    }


    public void manualUpdateAlipayBillExtend(String businessNo, String customerCode, String updateUser){
        CustomerInfo customerInfo = customerInfoMapper.getByCustomerCode(customerCode);
        if(customerInfo !=null){
            AlipayBillExtend alipayBillExtend = alipayBillExtendMapper.getByBusinessNo(businessNo);
            alipayBillExtend.setMatchCustomerCode(customerInfo.getCustomerCode());
            alipayBillExtend.setMatchCustomerName(customerInfo.getCustomerName());
            alipayBillExtend.setMatchStatus("1");
            alipayBillExtend.setMatchContent("手工选择匹配");
            alipayBillExtend.setDef2(updateUser);
            alipayBillExtend.setDef3(fmt.format(new Date()));
            alipayBillExtendMapper.manualUpdateMatchStatus(alipayBillExtend);
        }
    }
}
