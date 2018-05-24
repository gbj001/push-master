package com.xinliangjishipin.pushwms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xinliangjishipin.pushwms.entity.OrderExchangeInfo;
import com.xinliangjishipin.pushwms.utils.Constants;
import com.xinliangjishipin.pushwms.utils.HttpClientUtils;
import com.xinliangjishipin.pushwms.utils.XmlUtil;
import okhttp3.Response;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.smartcardio.CardChannel;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class RemoteClientService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${push.order.process.result.fail.maxcount}")
    private String processResultFailMaxCount;

    @Value("${push.order.success}")
    private String pushOrderSuccess;

    @Value("${wms.wj.system}")
    private String wmsWJSystem;

    @Value("${wms.msp.system}")
    private String wmsMSPSystem;

    @Value("${wms.fl.client.customerid}")
    private String flCustomerId;

    @Value("${wms.fl.client.db}")
    private String fldb;

    @Value("${wms.fl.purchase.messageid}")
    private String flPurchaseMessageId;

    @Value("${wms.fl.sales.messageid}")
    private String flSalesMessageId;

    @Value("${wms.fl.appkey}")
    private String flAppKey;

    @Value("${wms.fl.apptoken}")
    private String flAppToken;

    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 调用封装的远程访问(json格式)
     */
    public void remoteClient(OrderExchangeInfo orderExchangeInfo, String url, String json, String wmsSystem, String transDataType){
        try {
            logger.info("start remoteClient...........");
            Response response = new HttpClientUtils().post(url, json, transDataType);
            orderExchangeInfo.setRequestContent(json);
            orderExchangeInfo.setResponseTime(new Date());
            JSONObject jsonObject = JSON.parseObject(response.body().string());
            logger.info("response data:" + jsonObject.toString());
            boolean isSuccess = false;
            if (wmsSystem.equals(wmsMSPSystem)) {
                isSuccess = Objects.equals(jsonObject.get("status").toString(), "success");
            }
            if (wmsSystem.equals(wmsWJSystem)) {
                isSuccess = Objects.equals(jsonObject.get("resCode"), 200);
            }
            if (response.isSuccessful()) {
                orderExchangeInfo.setResponseContent(jsonObject.toString());
                boolean flag = isSuccess || (orderExchangeInfo.getPushProcessCount() > Integer.parseInt(processResultFailMaxCount) && !isSuccess);
                if (flag) {
                    orderExchangeInfo.setPushStatus(pushOrderSuccess);
                } else {
                    orderExchangeInfo.setPushProcessCount(orderExchangeInfo.getPushProcessCount() + 1);
                }
            } else {
                orderExchangeInfo.setResponseContent(jsonObject.toString());
            }
            logger.info("end remoteClient...........");
        } catch(JSONException ex){
            logger.error("JSONException parse fail:" + ex.getMessage());
            orderExchangeInfo.setResponseContent(ex.getMessage());
        }catch (IOException e) {
            logger.error("remoteClient fail:" + e.getMessage());
            orderExchangeInfo.setResponseContent(e.getMessage());
        }
    }


    /**
     * 调用封装的远程访问(xml格式)
     */
    public void remoteXmlClient(OrderExchangeInfo orderExchangeInfo, String url, String sign, String xmlData, String orderType) {
        try {
            logger.info("start remoteXmlClient...........");
            Map paramMap = new HashMap();
            if(Constants.FL_PURCHASE_ORDER_TYPE.equals(orderType)){
                paramMap.put("method", "putASNData");
                paramMap.put("messageid", flPurchaseMessageId);
            }else{
                paramMap.put("method", "putSOData");
                paramMap.put("messageid", flSalesMessageId);
            }
            paramMap.put("client_customerid", flCustomerId);
            paramMap.put("client_db", fldb);
            paramMap.put("apptoken", flAppToken);
            paramMap.put("appkey", flAppKey);
            paramMap.put("sign", sign);
            paramMap.put("timestamp", fmt.format(new Date()));
            paramMap.put("data", xmlData);

            Response response = new HttpClientUtils().xmlPost(url, paramMap);
            orderExchangeInfo.setRequestContent(xmlData);
            orderExchangeInfo.setResponseTime(new Date());
            String returnValue = URLDecoder.decode(response.body().string(),"utf-8");
            Element root = XmlUtil.StringToFLPurchaseXml(returnValue);
            String returnFlag = root.element("returnFlag").getText();
            this.processResult(response, orderExchangeInfo, returnValue, returnFlag);
            logger.info("end remoteXmlClient...........");
        } catch (IOException e) {
            logger.error("remoteXmlClient fail:" + e.getMessage());
            orderExchangeInfo.setResponseContent(e.getMessage());
        }
    }

    private void processResult(Response response, OrderExchangeInfo orderExchangeInfo, String returnValue, String returnFlag){
        boolean isSuccess = "1".equals(returnFlag);
        if (response.isSuccessful()) {
            orderExchangeInfo.setResponseContent(returnValue);
            boolean flag = isSuccess || (orderExchangeInfo.getPushProcessCount() > Integer.parseInt(processResultFailMaxCount) && !isSuccess);
            if (flag) {
                orderExchangeInfo.setPushStatus(pushOrderSuccess);
            } else {
                orderExchangeInfo.setPushProcessCount(orderExchangeInfo.getPushProcessCount() + 1);
            }
        } else {
            orderExchangeInfo.setResponseContent(returnValue);
        }
    }

}
