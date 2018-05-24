package com.xinliangjishipin.pushwms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinliangjishipin.pushwms.entity.AlipayBillExtend;
import com.xinliangjishipin.pushwms.utils.HttpClientUtils;
import com.xinliangjishipin.pushwms.utils.WebServiceClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Service
public class SendAlipayBillService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SendAlipayBillService sendAlipayBillService;

    @Value("${send.source}")
    private String source;

    @Value("${send.org}")
    private String org;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 调用NC的远程访问
     */
    public AlipayBillExtend remoteClient(AlipayBillExtend alipayBillExtend, String url, String json, String transDataType) {
        try {
            logger.info("start sendAlipayBill...........");
            WebServiceClient webServiceClient = new WebServiceClient();
            String xml = sendAlipayBillService.convertToXml(alipayBillExtend);
            String returnValue = webServiceClient.remoteClient(url,xml);
            //Response response = new HttpClientUtils().post(url, json, transDataType);
            //JSONObject jsonObject = JSON.parseObject(response.body().string());
            //logger.info("response data:" + jsonObject.toString());
            //boolean isSuccess = Objects.equals(jsonObject.get("resultcode").toString(), "1");
            //alipayBillExtend.setRequestStatus("2");
            //alipayBillExtend.setRequestText(json);
            //alipayBillExtend.setRequestTime(new Date());
            //
            //if (response.isSuccessful()) {
            //    alipayBillExtend.setResponseText(jsonObject.toString());
            //    alipayBillExtend.setResponseTime(new Date());
            //    if (isSuccess) {
            //        alipayBillExtend.setResponseStatus("Y");
            //    } else {
            //        alipayBillExtend.setResponseStatus("N");
            //    }
            //    logger.info("end remoteClient...........");
            //} else {
            //    alipayBillExtend.setResponseStatus("X");
            //    alipayBillExtend.setResponseTime(new Date());
            //    alipayBillExtend.setResponseText(jsonObject.toString());
            //}
        } catch (Exception e) {
            logger.error("remoteClient fail:" + e.getMessage());
            alipayBillExtend.setRequestStatus("2");
            alipayBillExtend.setRequestText(json);
            alipayBillExtend.setRequestTime(new Date());
            alipayBillExtend.setResponseStatus("X");
            alipayBillExtend.setResponseTime(new Date());
            alipayBillExtend.setResponseText(e.getMessage());
        }
        return alipayBillExtend;
    }

    /**
     * 转成成xml数据
     */
    public String convertToXml(AlipayBillExtend alipayBillExtend) {
        String def3 = alipayBillExtend.getSourceName() + "(" + alipayBillExtend.getSourceAccount() + ")";
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
        sb.append("<ufinterface billtype=\"F2\" filename=\"F2.xml\" replace=\"N\" sender=\"ALI\">");
        sb.append("<bill id=\"").append(alipayBillExtend.getBusinessNo()).append("\">");
        sb.append("<billhead>");
        sb.append("<pk_group>xlj</pk_group>");
        sb.append("<pk_org>").append(org).append("</pk_org>");
        sb.append("<pk_billtype>F2</pk_billtype>");
        sb.append("<billdate>").append(formatter.format(alipayBillExtend.getTradeTime())).append("</billdate>");
        sb.append("<pk_currtype>CNY</pk_currtype>");
        sb.append("<recaccount>0200253019000155462</recaccount>");
        sb.append("<money>").append(alipayBillExtend.getIncomeAmount()).append("</money>");
        sb.append("<customer>").append(StringUtils.isEmpty(alipayBillExtend.getMatchCustomerCode()) ? "" : alipayBillExtend.getMatchCustomerCode()).append("</customer>");
        sb.append("<def1>").append(source).append("</def1>");
        sb.append("<def2>").append(alipayBillExtend.getBusinessNo()).append("</def2>");
        sb.append("<def3>").append(def3).append("</def3>");
        sb.append("<bodys>");
        sb.append("<item>");
        sb.append("<pk_recpaytype>001</pk_recpaytype>");
        sb.append("<customer>").append(alipayBillExtend.getMatchCustomerCode() == null ? "" : alipayBillExtend.getMatchCustomerCode()).append("</customer>");
        sb.append("<money_cr>").append(alipayBillExtend.getIncomeAmount()).append("</money_cr>");
        sb.append("<pk_balatype>12</pk_balatype>");
        sb.append("<scomment>").append(StringUtils.isEmpty(alipayBillExtend.getRemark()) ? alipayBillExtend.getCreatedUser() + "手工录入" : alipayBillExtend.getRemark()).append("</scomment>");
        sb.append("</item>");
        sb.append("</bodys>");
        sb.append("</billhead>");
        sb.append("</bill>");
        sb.append("</ufinterface>");
        return sb.toString();
    }

    public String parseXml(String xmlDate){
        return "";
    }

}
