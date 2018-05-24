package com.xinliangjishipin.pushwms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinliangjishipin.pushwms.dto.PushDeliveryOrder;
import com.xinliangjishipin.pushwms.dto.PushPurchaseOrder;
import com.xinliangjishipin.pushwms.dto.PushSaleOrder;
import com.xinliangjishipin.pushwms.entity.*;
import com.xinliangjishipin.pushwms.mapper.OrderExchangeInfoMapper;
import com.xinliangjishipin.pushwms.utils.DateUtil;
import com.xinliangjishipin.pushwms.utils.XmlUtil;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gengbeijun
 */
@Service
public class OrderExchangeInfoService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PushPurchaseService pushPurchaseService;

    @Autowired
    private PushSalesOrderService pushSalesOrderService;

    @Autowired
    private PushDeliveryOrderService pushDeliveryOrderService;

    @Autowired
    private OrderExchangeInfoMapper orderExchangeInfoMapper;

    @Autowired
    private SendMailService sendMailService;

    @Value("${push.order.porder}")
    private String pushOrderPOrder;

    @Value("${push.order.saleOrder}")
    private String pushSaleOrderType;

    @Value("${push.order.saleTasteOrder}")
    private String pushSaleTasteOrder;

    @Value("${push.order.saleBackOrder}")
    private String pushSaleBackOrder;

    @Value("${push.order.saleDeliveryOrder}")
    private String pushDeliveryOrder;

    @Value("${send.mail.maxcount}")
    private String sendMailMaxCount;

    @Value("${wms.wj.system}")
    private String wmsWJSystem;

    @Value("${wms.msp.system}")
    private String wmsMSPSystem;

    @Value("${wms.fl.system}")
    private String wmsFLSystem;

    @Value("${send.wms.push.mail}")
    private String mailReceiver;

    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 采购订单推送处理方法
     */
    public void pushPurchaseOrderProcess() {
        logger.info("start purchase order .........");
        List<PushPurchaseOrder> resultPOrder = pushPurchaseService.outputPushPoOrder();
        pushPurchaseService.pushOrder(resultPOrder);
        logger.info("end purchase order.....");
    }

    /**
     * 销售订单推送处理方法
     */
    public void pushSaleOrderProcess() {
        logger.info("start sale order.........");
        List<PushSaleOrder> resultPOrder = pushSalesOrderService.outputPushSaleOrder();
        pushSalesOrderService.pushOrder(resultPOrder);
        logger.info("end sale order.....");
    }

    /**
     * 发货单推送处理方法
     */
    public void saleDeliveryProcess() {
        logger.info("start delivery order .........");
        List<PushDeliveryOrder> resultPOrder = pushDeliveryOrderService.outputPushDeliveryOrder();
        pushDeliveryOrderService.pushDeliveryOrder(resultPOrder);
        logger.info("end delivery order .....");
    }

    /**
     * 监控发送失败订单
     * */
    public void monitoredPushOrder() {
        logger.info("start monitored push order order .........");
        List<OrderExchangeInfo> resultOrder = orderExchangeInfoMapper.failPushOrderList();
        resultOrder = resultOrder.stream().filter(n -> !StringUtils.isEmpty(n.getPushChannel())).collect(Collectors.toList());
        if(resultOrder.size() > 0){
            StringBuilder context = new StringBuilder();
            for(OrderExchangeInfo orderExchangeInfo: resultOrder){
                String message = "";
                if(wmsWJSystem.equals(orderExchangeInfo.getPushChannel().toUpperCase()) || wmsMSPSystem.equals(orderExchangeInfo.getPushChannel().toUpperCase())){
                    JSONObject jsonObject = JSON.parseObject(orderExchangeInfo.getResponseContent());
                    if(wmsMSPSystem.equals(orderExchangeInfo.getPushChannel().toUpperCase())){
                        message = jsonObject.get("message").toString();
                    }
                    if(wmsWJSystem.equals(orderExchangeInfo.getPushChannel().toUpperCase())){
                        message = jsonObject.get("resMsg").toString();
                    }
                }
                if(wmsFLSystem.equals(orderExchangeInfo.getPushChannel().toUpperCase())){
                    Element element = XmlUtil.StringToFLResultXml(orderExchangeInfo.getResponseContent());
                    message = element.element("returnDesc").getText();
                }
                context.append(WMSType.getNameByCode(orderExchangeInfo.getPushChannel()))
                        .append(" 订单类型:").append(ExchangeInfoOrderType.getNameByCode(orderExchangeInfo.getOrderType()))
                        .append(" 订单号:").append(orderExchangeInfo.getOutOrderId())
                        .append(" 失败原因：").append(message).append("<br/>");
            }

            try {
                sendMailService.sendHtmlMail("推送给WMS失败的订单",context.toString()+"<br> 请根据订单号及时登录NC系统进行处理！", mailReceiver);
            } catch (Exception e) {
                logger.info("send mail fail ....." + e.getMessage());
            }
        }
        logger.info("end monitored order .....");
    }

    public void updateOrderExchangeInfo(OrderExchangeInfo orderExchangeInfo, PurchaseOrder purchaseOrder, SaleOrder saleOrder, DeliveryOrder deliveryOrder){
        if (orderExchangeInfo == null) {
            OrderExchangeInfo newOrderExchangeInfo = null;
            if(!StringUtils.isEmpty(purchaseOrder)){
                newOrderExchangeInfo = new OrderExchangeInfo(purchaseOrder, pushOrderPOrder);
            }
            if(!StringUtils.isEmpty(saleOrder)){
                //销售订单默认为2-普通销售
                String salesOrderType = pushSaleOrderType;
                if(!StringUtils.isEmpty(saleOrder)){
                    if(saleOrder.getOrderType().equals(SaleType.GENERAL.getInType())){
                        salesOrderType = pushSaleOrderType;
                    }
                    else if(saleOrder.getOrderType().equals(SaleType.BACK.getInType())){
                        salesOrderType = pushSaleBackOrder;
                    }
                    else if(saleOrder.getOrderType().equals(SaleType.TASTE.getInType())){
                        salesOrderType = pushSaleTasteOrder;
                    }
                }
                newOrderExchangeInfo = new OrderExchangeInfo(saleOrder, salesOrderType);
            }
            if(!StringUtils.isEmpty(deliveryOrder)){
                newOrderExchangeInfo = new OrderExchangeInfo(deliveryOrder, pushDeliveryOrder);
            }
            orderExchangeInfoMapper.insertOrderExchangeInfo(newOrderExchangeInfo);
        } else {
            orderExchangeInfo.setUpdatedUser("");
            orderExchangeInfo.setPushChannel("");
            orderExchangeInfo.setResponseContent("");
            if((purchaseOrder != null && (DateUtil.compare_date(purchaseOrder.getAuditTime(), fmt.format(orderExchangeInfo.getUpdatedTime()), DATE_FORMAT) ==1))
                    ||(saleOrder != null && (DateUtil.compare_date(saleOrder.getAuditTime(), fmt.format(orderExchangeInfo.getUpdatedTime()), DATE_FORMAT) ==1))
                    ||(deliveryOrder != null && (DateUtil.compare_date(deliveryOrder.getAuditTime(), fmt.format(orderExchangeInfo.getUpdatedTime()), DATE_FORMAT) ==1))){
                orderExchangeInfo.setPushStatus("0");
                orderExchangeInfo.setUpdatedTime(new Date());
            }
            orderExchangeInfoMapper.updateOrderExchangeInfo(orderExchangeInfo);
        }
    }
}
