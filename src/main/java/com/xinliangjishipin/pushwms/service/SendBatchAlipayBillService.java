package com.xinliangjishipin.pushwms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.xinliangjishipin.pushwms.entity.AlipayBillExtend;
import com.xinliangjishipin.pushwms.entity.ResultResponse;
import com.xinliangjishipin.pushwms.mapper.AlipayBillExtendMapper;
import com.xinliangjishipin.pushwms.utils.HttpClientUtils;
import com.xinliangjishipin.pushwms.utils.WebServiceClient;
import com.xinliangjishipin.pushwms.utils.XmlUtil;
import com.xinliangjishipin.pushwms.webservice.CallbackClient;
import okhttp3.Response;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class SendBatchAlipayBillService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AlipayBillExtendMapper alipayBillExtendMapper;

    @Autowired
    private SendAlipayBillService sendAlipayBillService;

    /**
     * 批量发送已经匹配的支付宝账单信息
     */
    public void sendBatchAlipayBill() {
        List<AlipayBillExtend> alipayBillExtendList = alipayBillExtendMapper.sendMatchedList();
        int successCount = 0;
        int failCount = 0;
        for(AlipayBillExtend alipayBillExtend: alipayBillExtendList){
            String xml = sendAlipayBillService.convertToXml(alipayBillExtend);
            //调用NC系统并等待返回结果
            String returnValue = CallbackClient.callBackToNC(xml);
            Element root = XmlUtil.StringToAlipayBillXml(returnValue);
            String resultCode = root.element("resultcode").getText();
            String resultDescription = root.element("resultdescription").getText();
            String content = root.element("content").getText();

            alipayBillExtend.setRequestStatus("1");
            alipayBillExtend.setRequestTime(new Date());
            alipayBillExtend.setRequestText(xml);

            if("1".equals(resultCode)){
                alipayBillExtend.setResponseTime(new Date());
                alipayBillExtend.setResponseStatus("Y");
                alipayBillExtend.setResponseText(content);
            }else{
                alipayBillExtend.setResponseTime(new Date());
                alipayBillExtend.setResponseStatus("N");
                alipayBillExtend.setResponseText(resultDescription);
            }
            alipayBillExtendMapper.updateSendAlipayBillExtend(alipayBillExtend);


            if("Y".equals(alipayBillExtend.getResponseStatus())){
                logger.info("添加支付宝账单("+alipayBillExtend.toString()+")成功，并已生成收款单！请到NC系统查看！");
                successCount++;
            }
            if("N".equals(alipayBillExtend.getResponseStatus())){
                logger.info("添加支付宝账单("+alipayBillExtend.toString()+")成功，但并未生成收款单！信息为：" + alipayBillExtend.getResponseText());
                failCount++;
            }
            if("X".equals(alipayBillExtend.getResponseStatus())){
                logger.info("远程连接未响应,信息为：" + alipayBillExtend.getResponseText());
                failCount++;
            }
        }

        logger.info("本次共发送支付宝账单 " + alipayBillExtendList.size() + " 条，其中 " + successCount + " 条成功，" + failCount + " 条失败。");
    }
}
