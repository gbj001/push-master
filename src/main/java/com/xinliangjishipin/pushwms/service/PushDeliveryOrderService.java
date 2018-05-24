package com.xinliangjishipin.pushwms.service;

import com.xinliangjishipin.pushwms.dto.PushDeliveryOrder;
import com.xinliangjishipin.pushwms.dto.PushSaleOrder;
import com.xinliangjishipin.pushwms.entity.*;
import com.xinliangjishipin.pushwms.mapper.*;
import com.xinliangjishipin.pushwms.utils.Base64Util;
import com.xinliangjishipin.pushwms.utils.Constants;
import com.xinliangjishipin.pushwms.utils.DateUtil;
import com.xinliangjishipin.pushwms.utils.MD5EncoderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gengbeijun
 */
@Service
public class PushDeliveryOrderService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderExchangeInfoMapper orderExchangeInfoMapper;

    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;

    @Autowired
    private DeliveryOrderDetailMapper deliveryOrderDetailMapper;

    @Autowired
    private WarehouseConfigMapper warehouseConfigMapper;

    @Autowired
    private RemoteClientService remoteClientService;

    @Autowired
    private OrderExchangeInfoService orderExchangeInfoService;

    @Value("${push.order.fail}")
    private String pushOrderFail;

    @Value("${push.order.process.result.fail.maxcount}")
    private String processResultFailMaxCount;

    @Value("${push.order.success}")
    private String pushOrderSuccess;

    @Value("${push.order.maxcount}")
    private int pushOrderMaxCount;

    @Value("${wms.wj.system}")
    private String wmsWJSystem;

    @Value("${wms.msp.system}")
    private String wmsMSPSystem;

    @Value("${wms.fl.system}")
    private String wmsFLSystem;

    @Value("${wms.fl.url}")
    private String flUrl;

    @Value("${wms.fl.customerId}")
    private String wmsFLCustomerID;

    @Value("${wms.fl.appsecrt}")
    private String flAppsecrt;

    @Value("${wms.wj.url}")
    private String wjUrl;

    @Value("${wms.wj.appId}")
    private String appId;

    @Value("${wms.wj.secretKey}")
    private String secretKey;

    @Value("${wms.wj.companyCode}")
    private String wjCompanyCode;

    @Value("${wms.msp.sale.url}")
    private String mspUrl;

    @Value("${send.host}")
    private String host;

    @Value("${purchase.order.ctrantype}")
    private String cTranType;

    @Value("${json.trans.data.type}")
    private String jsonTransData;

    @Value("${xml.trans.data.type}")
    private String xmlTransData;

    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat fmt = new SimpleDateFormat("");

    /**
     * 销售发货单推送
     */
    public void pushDeliveryOrder(List<PushDeliveryOrder> resultPOrder) {
        for (PushDeliveryOrder pushDeliveryOrder : resultPOrder) {
            //查询第三方wms配置表
            WarehouseConfig warehouseConfig = warehouseConfigMapper.getByNcWarehouseCode(pushDeliveryOrder.getWarehouseCode().toUpperCase());
            if (warehouseConfig == null) {
                OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(pushDeliveryOrder.getBillCode());
                logger.info("仓库编码不存在对应wms系统仓库编码,发货单号为：" + pushDeliveryOrder.getBillCode() + "，对应仓库编码为：" + pushDeliveryOrder.getWarehouseCode());
                orderExchangeInfo.setRequestContent("仓库编码不存在对应wms系统仓库编码,销售单号为：" + pushDeliveryOrder.getBillCode() + "，对应仓库编码为：" + pushDeliveryOrder.getWarehouseCode());
                orderExchangeInfo.setPushChannel("");
                orderExchangeInfo.setUpdatedUser("");
                orderExchangeInfoMapper.updateOrderExchangeInfo(orderExchangeInfo);
                continue;
            }
            OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(pushDeliveryOrder.getBillCode());

            String pushStartTime = warehouseConfig.getStartTime();
            String pushEndTime = warehouseConfig.getEndTime();

            if (warehouseConfig.getWmsShort().equals(wmsWJSystem) && DateUtil.dateInStartTimeAndEndTime(pushDeliveryOrder.getAuditTime(), pushStartTime, pushEndTime, DATE_FORMAT)) {
                //转换成wj需要的json格式数据
                String json = convertToWJJson(pushDeliveryOrder, warehouseConfig.getWmsWarehouseCode());
                remoteClientService.remoteClient(orderExchangeInfo, wjUrl, json, wmsWJSystem, jsonTransData);
                orderExchangeInfo.setPushChannel(wmsWJSystem);
            }
            if (warehouseConfig.getWmsShort().equals(wmsMSPSystem) && DateUtil.dateInStartTimeAndEndTime(pushDeliveryOrder.getAuditTime(), pushStartTime, pushEndTime, DATE_FORMAT)) {
                //转换成wj需要的json格式数据
                String json = convertToMSPJson(pushDeliveryOrder, warehouseConfig.getWmsWarehouseCode());
                remoteClientService.remoteClient(orderExchangeInfo, mspUrl, json, wmsMSPSystem, jsonTransData);
                orderExchangeInfo.setPushChannel(wmsMSPSystem);
            }
            if (warehouseConfig.getWmsShort().equals(wmsFLSystem) && DateUtil.dateInStartTimeAndEndTime(pushDeliveryOrder.getAuditTime(), pushStartTime, pushEndTime, DATE_FORMAT)) {
                //转换成fl需要的xml格式数据
                String xmlData = convertToFLXml(pushDeliveryOrder, warehouseConfig.getWmsWarehouseCode());
                String secretXmlData = flAppsecrt + xmlData + flAppsecrt;
                String sign = "";
                try {
                    sign = URLEncoder.encode(Base64Util.getBase64(MD5EncoderUtil.MD5Encoder(secretXmlData)).toUpperCase(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                remoteClientService.remoteXmlClient(orderExchangeInfo, flUrl, sign, xmlData, Constants.FL_SALES_ORDER_TYPE);
                orderExchangeInfo.setPushChannel(wmsFLSystem);
            }

            orderExchangeInfo.setUpdatedUser("");
            orderExchangeInfo.setUpdatedTime(new Date());
            orderExchangeInfoMapper.updateOrderExchangeInfo(orderExchangeInfo);
        }
    }

    /**
     * 从NC获取需要推送的销售订单
     */
    public List<PushDeliveryOrder> outputPushDeliveryOrder() {
        List<PushDeliveryOrder> resultPOrder = new ArrayList<PushDeliveryOrder>();

        //一. 查询已审核的发货单主表
        List<DeliveryOrder> deliveryOrderList = deliveryOrderMapper.getDeliveryOrders();
        for (DeliveryOrder deliveryOrder : deliveryOrderList) {
            OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(deliveryOrder.getBillCode());
            //1、过滤掉已经推送成功的发货单
            //1、过滤掉已经推送成功的采购订单
            boolean orderExchangeInfoIsNull = orderExchangeInfo == null;
            boolean pushStatus = (orderExchangeInfo != null && orderExchangeInfo.getPushStatus().equals(pushOrderFail) && orderExchangeInfo.getPushCount() < pushOrderMaxCount);
            boolean isChangeAuditTime = (orderExchangeInfo != null && DateUtil.compare_date(deliveryOrder.getAuditTime(), fmt.format(orderExchangeInfo.getUpdatedTime()), DATE_FORMAT) == 1);
            boolean flag = orderExchangeInfoIsNull || pushStatus || isChangeAuditTime;
            // boolean flag = orderExchangeInfo == null || (orderExchangeInfo != null && orderExchangeInfo.getPushStatus().equals(pushOrderFail) && orderExchangeInfo.getPushCount() < pushOrderMaxCount);
            if (flag) {
                PushDeliveryOrder pushDeliveryOrder = new PushDeliveryOrder(deliveryOrder);
                orderExchangeInfoService.updateOrderExchangeInfo(orderExchangeInfo, null, null, deliveryOrder);
                //根据销发货单主ID获取发货单明细
                List<DeliveryOrderDetail> allDeliveryOrderDetailList = deliveryOrderDetailMapper.getDeliveryOrderDetail(deliveryOrder.getPkOrder());
                //过滤掉运费的明细
                allDeliveryOrderDetailList = allDeliveryOrderDetailList.stream().filter(n -> !"W005009".equals(n.getMaterialCode())).collect(Collectors.toList());
                //过滤掉订单明细中包含多个仓库的发货单
                if (!isSync(allDeliveryOrderDetailList, deliveryOrder.getPkOrder())) {
                    continue;
                }

                List<DeliveryOrderDetail> deliveryOrderDetailList = new ArrayList<>();
                for (DeliveryOrderDetail deliveryOrderDetail : allDeliveryOrderDetailList) {
                    deliveryOrderDetail.setBillCode(deliveryOrder.getBillCode());
                    deliveryOrderDetail.setPkOrderHeader(deliveryOrder.getPkOrder());
                    deliveryOrderDetailList.add(deliveryOrderDetail);
                }

                pushDeliveryOrder.setWarehouseCode(deliveryOrderDetailList.get(0).getWarehouseCode());
                pushDeliveryOrder.setOrderType(deliveryOrder.getOrderType());
                pushDeliveryOrder.setItems(deliveryOrderDetailList);
                pushDeliveryOrder.setAuditTime(deliveryOrder.getAuditTime());
                resultPOrder.add(pushDeliveryOrder);
            }
        }
        return resultPOrder;
    }

    /**
     * 转换成唯捷需要的json格式
     */
    public String convertToWJJson(PushDeliveryOrder pushDeliveryOrder, String wmswarehouseCode) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder bizContent = new StringBuilder();
        String address = this.selectAddress(pushDeliveryOrder);
        String orderCause = pushDeliveryOrder.getOrderType().equals("30-02") ? "2" : "3";
        stringBuilder.append("{\"action\": \"weijie.wms.order.client.add\",\"appId\":\"").append(appId).append("\",");

        bizContent.append("{\\\"orderTime\\\":\\\"").append(fmt.format(pushDeliveryOrder.getBillDate()))
                .append("\\\",\\\"wjCompanyCode\\\": \\\"").append(wjCompanyCode)
                .append("\\\",\\\"wjWarehouseCode\\\": \\\"").append(wmswarehouseCode)
                .append("\\\",\\\"orderCause\\\": \\\"").append(orderCause)
                .append("\\\",\\\"senderName\\\": \\\"").append("")
                .append("\\\",\\\"senderPhone\\\": \\\"").append("13800000000")

                .append("\\\",\\\"senderProvinceCode\\\": \\\"").append("")
                .append("\\\",\\\"senderProvince\\\": \\\"").append("")
                .append("\\\",\\\"senderCityCode\\\": \\\"").append("")
                .append("\\\",\\\"senderCity\\\": \\\"").append("")
                .append("\\\",\\\"senderCountyCode\\\": \\\"").append("")
                .append("\\\",\\\"senderCounty\\\": \\\"").append("")
                .append("\\\",\\\"senderAddress\\\": \\\"").append("北京市朝阳区将台路丽都维景酒店")

                .append("\\\",\\\"receiverName\\\": \\\"").append(pushDeliveryOrder.getReceiverName())
                .append("\\\",\\\"receiverPhone\\\": \\\"").append(pushDeliveryOrder.getReceiverMobile())
                .append("\\\",\\\"receiverProvinceCode\\\": \\\"").append("")
                .append("\\\",\\\"receiverCityCode\\\": \\\"").append("")
                .append("\\\",\\\"receiverCity\\\": \\\"").append("")
                .append("\\\",\\\"receiverCounty\\\": \\\"").append("")
                .append("\\\",\\\"receiverAddress\\\": \\\"").append(address)

                .append("\\\",\\\"payWay\\\": \\\"").append("0")
                .append("\\\",\\\"upstreamNumber\\\": \\\"").append(pushDeliveryOrder.getBillCode())
                .append("\\\",\\\"remark\\\": \\\"").append(pushDeliveryOrder.getRemark() != null ? pushDeliveryOrder.getRemark() : "")
                .append("\\\",");
        bizContent.append("\\\"goods\\\": [");
        for (DeliveryOrderDetail deliveryOrderDetail : pushDeliveryOrder.getItems()) {
            boolean flag = this.isTransferChangeRate(deliveryOrderDetail.getTransRate());
            double goodsCount = flag ? deliveryOrderDetail.getQuantity() : deliveryOrderDetail.getMainQuantity();
            String goodsUnit = flag ? deliveryOrderDetail.getQuantityUnits() : deliveryOrderDetail.getMainQuantityUnits();

            bizContent.append("{\\\"goodsName\\\": \\\"").append(deliveryOrderDetail.getMaterialName())
                    .append("\\\",\\\"goodsNo\\\": \\\"").append(deliveryOrderDetail.getMaterialCode())
                    .append("\\\",\\\"goodsCount\\\": \\\"").append(goodsCount)
                    .append("\\\",\\\"goodsUnit\\\": \\\"").append(goodsUnit)
                    //.append("\\\",\\\"goodsAuxiliaryCount\\\": \\\"").append(saleOrderDetail.getMainQuantity())
                    //.append("\\\",\\\"goodsAuxiliaryUnit\\\": \\\"").append(saleOrderDetail.getMainQuantityUnits())
                    .append("\\\",\\\"changeRate\\\": \\\"").append(deliveryOrderDetail.getTransRate())
                    .append("\\\",\\\"isGift\\\": \\\"").append(deliveryOrderDetail.getIsGift())
                    .append("\\\",\\\"extendInfo\\\":{\\\"ctrantypeid\\\":\\\"").append(SaleType.getOutTypeByInType(pushDeliveryOrder.getOrderType()))
                    .append("\\\",\\\"csourcetype\\\": \\\"").append("4331")
                    .append("\\\",\\\"csourcebillhid\\\": \\\"").append(deliveryOrderDetail.getPkOrderHeader())
                    .append("\\\",\\\"csourcebillbid\\\": \\\"").append(deliveryOrderDetail.getPkOrderBody())
                    .append("\\\",\\\"vsourcebillcode\\\": \\\"").append(pushDeliveryOrder.getBillCode()).append("\\\"},")
                    .append("\\\"remark\\\": \\\"").append("").append("\\\"},");
        }
        bizContent = bizContent.delete(bizContent.length() - 1, bizContent.length());
        bizContent.append("]");
        bizContent.append("}\"");

        StringBuilder signStr = new StringBuilder();
        signStr.append(bizContent.delete(bizContent.length() - 1, bizContent.length()).toString().replaceAll("\\\\", ""));
        String sign = MD5EncoderUtil.MD5Encoder(signStr.toString() + secretKey);

        stringBuilder.append("\"sign\": \"").append(sign).append("\",");
        stringBuilder.append("\"charset\": \"utf-8\",\"version\": \"1.0\",\"timestamp\":\"").append(fmt.format(new Date())).append("\",");

        stringBuilder.append(bizContent.insert(0, "\"bizContent\":\""));
        stringBuilder.append("\"}");
        return stringBuilder.toString();
    }

    /**
     * 转换成码上配需要的json格式
     */
    public String convertToMSPJson(PushDeliveryOrder pushDeliveryOrder, String wmsWarehouseCode) {
        //只有退货入库传1 其他都传0
        String orderType = pushDeliveryOrder.getOrderType().equals("30-02") ? "1" : "0";
        StringBuilder stringBuilder = new StringBuilder();
        String address = this.selectAddress(pushDeliveryOrder);
        stringBuilder.append("{\"order\": {\"warehouse_number\":\"").append(wmsWarehouseCode)
                .append("\",\"ctrantypeid\": \"").append(SaleType.getOutTypeByInType(pushDeliveryOrder.getOrderType()))
                .append("\",\"order_type\": \"").append(orderType)
                .append("\",\"customer_number\": \"").append(pushDeliveryOrder.getBillCode())
                .append("\",\"delivery_address\": \"").append(address)
                .append("\",\"sign_up\": \"").append(pushDeliveryOrder.getReceiverName())
                .append("\",\"sign_up_tel\": \"").append(pushDeliveryOrder.getReceiverMobile())
                .append("\",\"remarks\": \"").append(pushDeliveryOrder.getRemark()).append("\"},");
        stringBuilder.append("\"goods\": [");
        for (DeliveryOrderDetail deliveryOrderDetail : pushDeliveryOrder.getItems()) {
            boolean flag = this.isTransferChangeRate(deliveryOrderDetail.getTransRate());
            double goodsCount = flag ? deliveryOrderDetail.getQuantity() : deliveryOrderDetail.getMainQuantity();
            stringBuilder.append("{\"goods_name\": \"").append(deliveryOrderDetail.getMaterialName())
                    .append("\",\"goods_number\": \"").append(deliveryOrderDetail.getMaterialCode())
                    .append("\",\"gifts\": \"").append(deliveryOrderDetail.getIsGift())
                    .append("\",\"specification\": \"").append(deliveryOrderDetail.getMaterialSpec())
                    .append("\",\"barcode\": \"\",\"color\": \"\",\"style\": \"\",\"weight\": \"\",\"volume\": \"")
                    //.append("\",\"large_unit_quantity\": \"").append(saleOrderDetail.getMainQuantity())
                    .append("\",\"small_unit_quantity\": \"").append(goodsCount)
                    .append("\",\"trans_rate\": \"").append(deliveryOrderDetail.getTransRate())
                    .append("\",\"cost\": \"\",\"temperature\": \"\",\"humidity\": \"\",\"csourcetype\": \"4331")
                    .append("\",\"csourcebillhid\": \"").append(deliveryOrderDetail.getPkOrderHeader())
                    .append("\",\"csourcebillbid\": \"").append(deliveryOrderDetail.getPkOrderBody())
                    .append("\",\"vsourcebillcode\": \"").append(pushDeliveryOrder.getBillCode())
                    .append("\",\"remark\": \"\"},");
        }
        stringBuilder = stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());

        stringBuilder.append("],");

        stringBuilder.append("\"client\": {\"name\": \"").append(pushDeliveryOrder.getCustomerName())
                .append("\",\"code\": \"").append(pushDeliveryOrder.getCustomerCode())
                .append("\",\"contact\": \"").append(pushDeliveryOrder.getReceiverName())
                .append("\",\"tel\": \"").append(pushDeliveryOrder.getReceiverMobile())
                .append("\",\"dtladdress\": \"").append(address).append("\"}");
        stringBuilder.append("}");

        return stringBuilder.toString();

    }

    public String convertToFLXml(PushDeliveryOrder pushDeliveryOrder, String wmsWarehouseCode) {
        StringBuilder sb = new StringBuilder();
        String address = this.selectAddress(pushDeliveryOrder);
        String orderType = pushDeliveryOrder.getOrderType().equals("30-02") ? "20" : "1";
        sb.append("<xmldata><header>");
        sb.append("<OrderNo>").append(pushDeliveryOrder.getBillCode()).append("</OrderNo>");
        sb.append("<OrderType>").append(orderType).append("</OrderType>");
        sb.append("<OrderTime>").append(fmt.format(pushDeliveryOrder.getBillDate())).append("</OrderTime>");
        sb.append("<CustomerID>").append(wmsFLCustomerID).append("</CustomerID>");
        sb.append("<ConsigneeID>").append("XN").append("</ConsigneeID>");
        sb.append("<ConsigneeName>").append("").append("</ConsigneeName>");
        sb.append("<C_Province>").append("").append("</C_Province>");
        sb.append("<C_City>").append("").append("</C_City>");
        sb.append("<C_Address1>").append(address).append("</C_Address1>");
        sb.append("<C_Address2>").append("").append("</C_Address2>");
        sb.append("<Notes>").append(pushDeliveryOrder.getRemark()).append("</Notes>");
        sb.append("<WarehouseID>").append(wmsWarehouseCode).append("</WarehouseID>");
        sb.append("<H_EDI_03>").append(SaleType.getOutTypeByInType(pushDeliveryOrder.getOrderType())).append("</H_EDI_03>");
        for (DeliveryOrderDetail deliveryOrderDetail : pushDeliveryOrder.getItems()) {
            boolean flag = this.isTransferChangeRate(deliveryOrderDetail.getTransRate());
            double goodsCount = flag ? deliveryOrderDetail.getQuantity() : deliveryOrderDetail.getMainQuantity();
            sb.append("<detailsItem>");
            sb.append("<LineNo>").append(deliveryOrderDetail.getCrowNo()).append("</LineNo>");
            sb.append("<CustomerID>").append(wmsFLCustomerID).append("</CustomerID>");
            sb.append("<SKU>").append(deliveryOrderDetail.getMaterialCode()).append("</SKU>");
            sb.append("<QtyOrdered>").append(goodsCount).append("</QtyOrdered>");
            sb.append("<Notes>").append("").append("</Notes>");
            sb.append("<D_EDI_03>").append(deliveryOrderDetail.getPkOrderBody()).append("</D_EDI_03>");
            sb.append("<D_EDI_04>").append(deliveryOrderDetail.getIsGift()).append("</D_EDI_04>");
            sb.append("<D_EDI_05>").append("4331").append("</D_EDI_05>");
            sb.append("<D_EDI_06>").append(pushDeliveryOrder.getBillCode()).append("</D_EDI_06>");
            sb.append("<D_EDI_07>").append(deliveryOrderDetail.getPkOrderHeader()).append("</D_EDI_07>");
            sb.append("<D_EDI_08>").append(deliveryOrderDetail.getTransRate()).append("</D_EDI_08>");
            sb.append("</detailsItem>");
        }
        sb.append("</header></xmldata>");
        return sb.toString();
    }

    /**
     * 过滤仓库编码不一致的采发货单
     */
    public boolean isSync(List<DeliveryOrderDetail> deliveryOrderDetailList, String pkOrderId) {
        boolean flag = false;
        Set<String> warehouseCodeSet = new HashSet<>();
        for (DeliveryOrderDetail deliveryOrderDetail : deliveryOrderDetailList) {
            warehouseCodeSet.add(deliveryOrderDetail.getWarehouseCode());
        }
        if (warehouseCodeSet.size() == 1) {
            flag = true;
        } else {
            logger.info("发货单明细中仓库编码大于1个：" + pkOrderId);
        }
        return flag;
    }

    private boolean isTransferChangeRate(String changeRate) {
        double molecular = Double.parseDouble(changeRate.substring(0, changeRate.indexOf("/")));
        double denominator = Double.parseDouble(changeRate.substring(changeRate.indexOf("/") + 1, changeRate.length()));
        return (molecular / denominator) < 1;
    }

    private String selectAddress(PushDeliveryOrder pushDeliveryOrder) {
        String address = "";
        if (Constants.OUT_ORDER_SOURCE.equals(pushDeliveryOrder.getOrderSource())) {
            address = pushDeliveryOrder.getProvince() + pushDeliveryOrder.getCity() + pushDeliveryOrder.getArea() + pushDeliveryOrder.getReceiverAddress();
        } else {
            address = pushDeliveryOrder.getReceiverAddress();
        }
        return address;
    }

}
