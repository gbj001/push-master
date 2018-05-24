package com.xinliangjishipin.pushwms.service;

import com.xinliangjishipin.pushwms.dto.PushSaleOrder;
import com.xinliangjishipin.pushwms.entity.*;
import com.xinliangjishipin.pushwms.mapper.OrderExchangeInfoMapper;
import com.xinliangjishipin.pushwms.mapper.SaleOrderDetailMapper;
import com.xinliangjishipin.pushwms.mapper.SaleOrderMapper;
import com.xinliangjishipin.pushwms.mapper.WarehouseConfigMapper;
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
public class PushSalesOrderService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderExchangeInfoMapper orderExchangeInfoMapper;

    @Autowired
    private SaleOrderMapper saleOrderMapper;

    @Autowired
    private SaleOrderDetailMapper saleOrderDetailMapper;

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
    SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);

    /**
     * 销售订单推送
     */
    public void pushOrder(List<PushSaleOrder> resultPOrder) {
        for (PushSaleOrder pushSaleOrder : resultPOrder) {
            //查询第三方wms配置表
            WarehouseConfig warehouseConfig = warehouseConfigMapper.getByNcWarehouseCode(pushSaleOrder.getWarehouseCode().toUpperCase());
            if (warehouseConfig == null) {
                OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(pushSaleOrder.getBillCode());
                logger.info("仓库编码不存在对应wms系统仓库编码,销售订单号为：" + pushSaleOrder.getBillCode() + "，对应仓库编码为：" + pushSaleOrder.getWarehouseCode());
                orderExchangeInfo.setRequestContent("仓库编码不存在对应wms系统仓库编码,销售单号为：" + pushSaleOrder.getBillCode() + "，对应仓库编码为：" + pushSaleOrder.getWarehouseCode());
                orderExchangeInfo.setPushChannel("");
                orderExchangeInfo.setUpdatedUser("");
                orderExchangeInfoMapper.updateOrderExchangeInfo(orderExchangeInfo);
                continue;
            }
            OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(pushSaleOrder.getBillCode());

            String pushStartTime = warehouseConfig.getStartTime();
            String pushEndTime = warehouseConfig.getEndTime();
            if (warehouseConfig.getWmsShort().equals(wmsWJSystem) && DateUtil.dateInStartTimeAndEndTime(pushSaleOrder.getAuditTime(), pushStartTime, pushEndTime, DATE_FORMAT)) {
                //转换成wj需要的json格式数据
                String json = convertToWJJson(pushSaleOrder, warehouseConfig.getWmsWarehouseCode());
                remoteClientService.remoteClient(orderExchangeInfo, wjUrl, json, wmsWJSystem, jsonTransData);
                orderExchangeInfo.setPushChannel(wmsWJSystem);
            }
            if (warehouseConfig.getWmsShort().equals(wmsMSPSystem) && DateUtil.dateInStartTimeAndEndTime(pushSaleOrder.getAuditTime(), pushStartTime, pushEndTime, DATE_FORMAT)) {
                //转换成wj需要的json格式数据
                String json = convertToMSPJson(pushSaleOrder, warehouseConfig.getWmsWarehouseCode());
                remoteClientService.remoteClient(orderExchangeInfo, mspUrl, json, wmsMSPSystem, jsonTransData);
                orderExchangeInfo.setPushChannel(wmsMSPSystem);
            }
            if (warehouseConfig.getWmsShort().equals(wmsFLSystem) && DateUtil.dateInStartTimeAndEndTime(pushSaleOrder.getAuditTime(), pushStartTime, pushEndTime, DATE_FORMAT)) {
                //转换成fl需要的xml格式数据
                String xmlData = convertToFLXml(pushSaleOrder, warehouseConfig.getWmsWarehouseCode());
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
    public List<PushSaleOrder> outputPushSaleOrder() {
        List<PushSaleOrder> resultPOrder = new ArrayList<PushSaleOrder>();

        //一. 查询已审核的销售单主表
        List<SaleOrder> saleOrderList = saleOrderMapper.getSaleOrders();
        for (SaleOrder saleOrder : saleOrderList) {
            OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(saleOrder.getBillCode());
            //1、过滤掉已经推送成功的销售订单
            //1、过滤掉已经推送成功的采购订单
            boolean orderExchangeInfoIsNull = orderExchangeInfo == null;
            boolean pushStatus = (orderExchangeInfo != null && orderExchangeInfo.getPushStatus().equals(pushOrderFail) && orderExchangeInfo.getPushCount() < pushOrderMaxCount);
            boolean isChangeAuditTime = (orderExchangeInfo != null && DateUtil.compare_date(saleOrder.getAuditTime(), fmt.format(orderExchangeInfo.getUpdatedTime()), DATE_FORMAT) ==1 );
            boolean flag =  orderExchangeInfoIsNull || pushStatus || isChangeAuditTime;
            //boolean flag = orderExchangeInfo == null || (orderExchangeInfo != null && orderExchangeInfo.getPushStatus().equals(pushOrderFail) && orderExchangeInfo.getPushCount() < pushOrderMaxCount);
            if (flag) {
                PushSaleOrder pushSaleOrder = new PushSaleOrder(saleOrder);
                orderExchangeInfoService.updateOrderExchangeInfo(orderExchangeInfo, null, saleOrder, null);
                //根据销售订单主ID获取销售订单明细
                List<SaleOrderDetail> allSaleOrderDetailList = saleOrderDetailMapper.getSaleOrderDetail(saleOrder.getPkOrder());
                //过滤掉运费的明细
                allSaleOrderDetailList = allSaleOrderDetailList.stream().filter(n -> !"W005009".equals(n.getMaterialCode())).collect(Collectors.toList());
                //过滤掉订单明细中包含多个仓库的采购单
                if (!isSync(allSaleOrderDetailList, saleOrder.getPkOrder())) {
                    continue;
                }

                List<SaleOrderDetail> saleOrderDetailList = new ArrayList<>();
                for (SaleOrderDetail saleOrderDetail : allSaleOrderDetailList) {
                    saleOrderDetail.setBillCode(saleOrder.getBillCode());
                    saleOrderDetail.setPkOrderHeader(saleOrder.getPkOrder());
                    saleOrderDetailList.add(saleOrderDetail);
                }

                pushSaleOrder.setWarehouseCode(saleOrderDetailList.get(0).getWarehouseCode());
                pushSaleOrder.setOrderType(saleOrder.getOrderType());
                pushSaleOrder.setItems(saleOrderDetailList);
                pushSaleOrder.setAuditTime(saleOrder.getAuditTime());
                resultPOrder.add(pushSaleOrder);

            }
        }
        return resultPOrder;
    }

    /**
     * 转换成唯捷需要的json格式
     */
    public String convertToWJJson(PushSaleOrder pushSaleOrder, String wmswarehouseCode) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder bizContent = new StringBuilder();
        String address = this.selectAddress(pushSaleOrder);
        String orderCause = pushSaleOrder.getOrderType().equals("30-02") ? "2" : "3";
        stringBuilder.append("{\"action\": \"weijie.wms.order.client.add\",\"appId\":\"").append(appId).append("\",");

        bizContent.append("{\\\"orderTime\\\":\\\"").append(fmt.format(pushSaleOrder.getBillDate()))
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

                .append("\\\",\\\"receiverName\\\": \\\"").append(pushSaleOrder.getReceiverName())
                .append("\\\",\\\"receiverPhone\\\": \\\"").append(pushSaleOrder.getReceiverMobile())
                .append("\\\",\\\"receiverProvinceCode\\\": \\\"").append("")
                .append("\\\",\\\"receiverCityCode\\\": \\\"").append("")
                .append("\\\",\\\"receiverCity\\\": \\\"").append("")
                .append("\\\",\\\"receiverCounty\\\": \\\"").append("")
                .append("\\\",\\\"receiverAddress\\\": \\\"").append(address)

                .append("\\\",\\\"payWay\\\": \\\"").append("0")
                .append("\\\",\\\"upstreamNumber\\\": \\\"").append(pushSaleOrder.getBillCode())
                .append("\\\",\\\"remark\\\": \\\"").append(pushSaleOrder.getRemark() != null ? pushSaleOrder.getRemark() : "")
                .append("\\\",");
        bizContent.append("\\\"goods\\\": [");
        for (SaleOrderDetail saleOrderDetail : pushSaleOrder.getItems()) {
            boolean flag = this.isTransferChangeRate(saleOrderDetail.getTransRate());
            double goodsCount = flag ? saleOrderDetail.getQuantity() : saleOrderDetail.getMainQuantity();
            String goodsUnit = flag ? saleOrderDetail.getQuantityUnits() : saleOrderDetail.getMainQuantityUnits();
            //1-代表是销售退货,数量变成正数
            if ("3".equals(orderCause)) {
                goodsCount = Math.abs(goodsCount);
            }

            bizContent.append("{\\\"goodsName\\\": \\\"").append(saleOrderDetail.getMaterialName())
                    .append("\\\",\\\"goodsNo\\\": \\\"").append(saleOrderDetail.getMaterialCode())
                    .append("\\\",\\\"goodsCount\\\": \\\"").append(goodsCount)
                    .append("\\\",\\\"goodsUnit\\\": \\\"").append(goodsUnit)
                    //.append("\\\",\\\"goodsAuxiliaryCount\\\": \\\"").append(saleOrderDetail.getMainQuantity())
                    //.append("\\\",\\\"goodsAuxiliaryUnit\\\": \\\"").append(saleOrderDetail.getMainQuantityUnits())
                    .append("\\\",\\\"changeRate\\\": \\\"").append(saleOrderDetail.getTransRate())
                    .append("\\\",\\\"isGift\\\": \\\"").append(saleOrderDetail.getIsGift())
                    .append("\\\",\\\"extendInfo\\\":{\\\"ctrantypeid\\\":\\\"").append(SaleType.getOutTypeByInType(pushSaleOrder.getOrderType()))
                    .append("\\\",\\\"csourcetype\\\": \\\"").append("30")
                    .append("\\\",\\\"csourcebillhid\\\": \\\"").append(saleOrderDetail.getPkOrderHeader())
                    .append("\\\",\\\"csourcebillbid\\\": \\\"").append(saleOrderDetail.getPkOrderBody())
                    .append("\\\",\\\"vsourcebillcode\\\": \\\"").append(pushSaleOrder.getBillCode()).append("\\\"},")
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
    public String convertToMSPJson(PushSaleOrder pushSaleOrder, String wmsWarehouseCode) {
        //只有退货入库传1 其他都传0
        String orderType = pushSaleOrder.getOrderType().equals("30-02") ? "1" : "0";
        StringBuilder stringBuilder = new StringBuilder();
        String address = this.selectAddress(pushSaleOrder);
        stringBuilder.append("{\"order\": {\"warehouse_number\":\"").append(wmsWarehouseCode)
                .append("\",\"ctrantypeid\": \"").append(SaleType.getOutTypeByInType(pushSaleOrder.getOrderType()))
                .append("\",\"order_type\": \"").append(orderType)
                .append("\",\"customer_number\": \"").append(pushSaleOrder.getBillCode())
                .append("\",\"delivery_address\": \"").append(address)
                .append("\",\"sign_up\": \"").append(pushSaleOrder.getReceiverName())
                .append("\",\"sign_up_tel\": \"").append(pushSaleOrder.getReceiverMobile())
                .append("\",\"remarks\": \"").append(pushSaleOrder.getRemark()).append("\"},");
        stringBuilder.append("\"goods\": [");
        for (SaleOrderDetail saleOrderDetail : pushSaleOrder.getItems()) {
            boolean flag = this.isTransferChangeRate(saleOrderDetail.getTransRate());
            double goodsCount = flag ? saleOrderDetail.getQuantity() : saleOrderDetail.getMainQuantity();
            //1-代表是销售退货,数量变成正数
            if ("1".equals(orderType)) {
                goodsCount = Math.abs(goodsCount);
            }
            stringBuilder.append("{\"goods_name\": \"").append(saleOrderDetail.getMaterialName())
                    .append("\",\"goods_number\": \"").append(saleOrderDetail.getMaterialCode())
                    .append("\",\"gifts\": \"").append(saleOrderDetail.getIsGift())
                    .append("\",\"specification\": \"").append(saleOrderDetail.getMaterialSpec())
                    .append("\",\"barcode\": \"\",\"color\": \"\",\"style\": \"\",\"weight\": \"\",\"volume\": \"")
                    //.append("\",\"large_unit_quantity\": \"").append(saleOrderDetail.getMainQuantity())
                    .append("\",\"small_unit_quantity\": \"").append(goodsCount)
                    .append("\",\"trans_rate\": \"").append(saleOrderDetail.getTransRate())
                    .append("\",\"cost\": \"\",\"temperature\": \"\",\"humidity\": \"\",\"csourcetype\": \"30")
                    .append("\",\"csourcebillhid\": \"").append(saleOrderDetail.getPkOrderHeader())
                    .append("\",\"csourcebillbid\": \"").append(saleOrderDetail.getPkOrderBody())
                    .append("\",\"vsourcebillcode\": \"").append(pushSaleOrder.getBillCode())
                    .append("\",\"remark\": \"\"},");
        }
        stringBuilder = stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());

        stringBuilder.append("],");

        stringBuilder.append("\"client\": {\"name\": \"").append(pushSaleOrder.getCustomerName())
                .append("\",\"code\": \"").append(pushSaleOrder.getCustomerCode())
                .append("\",\"contact\": \"").append(pushSaleOrder.getReceiverName())
                .append("\",\"tel\": \"").append(pushSaleOrder.getReceiverMobile())
                .append("\",\"dtladdress\": \"").append(address).append("\"}");
        stringBuilder.append("}");

        return stringBuilder.toString();

    }

    public String convertToFLXml(PushSaleOrder pushSaleOrder, String wmsWarehouseCode) {
        StringBuilder sb = new StringBuilder();
        String address = this.selectAddress(pushSaleOrder);

        String orderType = pushSaleOrder.getOrderType().equals("30-02") ? "20" : "1";
        //销售退库调用采购入库单接口，orderType 值为固定20
        sb.append("<xmldata><header>");
        sb.append("<OrderNo>").append(pushSaleOrder.getBillCode()).append("</OrderNo>");
        sb.append("<OrderType>").append(orderType).append("</OrderType>");
        sb.append("<OrderTime>").append(fmt.format(pushSaleOrder.getBillDate())).append("</OrderTime>");
        sb.append("<CustomerID>").append(wmsFLCustomerID).append("</CustomerID>");
        sb.append("<ConsigneeID>").append("XN").append("</ConsigneeID>");
        sb.append("<ConsigneeName>").append(pushSaleOrder.getReceiverName()).append("</ConsigneeName>");
        sb.append("<C_Tel1>").append(pushSaleOrder.getReceiverMobile()).append("</C_Tel1>");
        sb.append("<C_Tel2>").append(pushSaleOrder.getReceiverTel()).append("</C_Tel2>");
        sb.append("<C_Province>").append("").append("</C_Province>");
        sb.append("<C_City>").append("").append("</C_City>");
        sb.append("<C_Address1>").append(address).append("</C_Address1>");
        sb.append("<C_Address2>").append("").append("</C_Address2>");
        sb.append("<Notes>").append(pushSaleOrder.getRemark()).append("</Notes>");
        sb.append("<WarehouseID>").append(wmsWarehouseCode).append("</WarehouseID>");
        sb.append("<H_EDI_03>").append(SaleType.getOutTypeByInType(pushSaleOrder.getOrderType())).append("</H_EDI_03>");
        for (SaleOrderDetail saleOrderDetail : pushSaleOrder.getItems()) {
            boolean flag = this.isTransferChangeRate(saleOrderDetail.getTransRate());
            double goodsCount = flag ? saleOrderDetail.getQuantity() : saleOrderDetail.getMainQuantity();
            sb.append("<detailsItem>");
            sb.append("<LineNo>").append(saleOrderDetail.getCrowNo()).append("</LineNo>");
            sb.append("<CustomerID>").append(wmsFLCustomerID).append("</CustomerID>");
            sb.append("<SKU>").append(saleOrderDetail.getMaterialCode()).append("</SKU>");
            sb.append("<QtyOrdered>").append(goodsCount).append("</QtyOrdered>");
            sb.append("<Notes>").append("").append("</Notes>");
            sb.append("<D_EDI_03>").append(saleOrderDetail.getPkOrderBody()).append("</D_EDI_03>");
            sb.append("<D_EDI_04>").append(saleOrderDetail.getIsGift()).append("</D_EDI_04>");
            sb.append("<D_EDI_05>").append("30").append("</D_EDI_05>");
            sb.append("<D_EDI_06>").append(pushSaleOrder.getBillCode()).append("</D_EDI_06>");
            sb.append("<D_EDI_07>").append(saleOrderDetail.getPkOrderHeader()).append("</D_EDI_07>");
            sb.append("<D_EDI_08>").append(saleOrderDetail.getTransRate()).append("</D_EDI_08>");
            sb.append("</detailsItem>");
        }
        sb.append("</header></xmldata>");
        return sb.toString();
    }

    /**
     * 过滤仓库编码不一致的采购订单
     */
    public boolean isSync(List<SaleOrderDetail> saleOrderDetailList, String pkOrderId) {
        boolean flag = false;
        Set<String> warehouseCodeSet = new HashSet<>();
        for (SaleOrderDetail saleOrderDetail : saleOrderDetailList) {
            warehouseCodeSet.add(saleOrderDetail.getWarehouseCode());
        }
        if (warehouseCodeSet.size() == 1) {
            flag = true;
        } else {
            logger.info("销售订单明细中仓库编码大于1个：" + pkOrderId);
        }
        return flag;
    }

    private boolean isTransferChangeRate(String changeRate) {
        double molecular = Double.parseDouble(changeRate.substring(0, changeRate.indexOf("/")));
        double denominator = Double.parseDouble(changeRate.substring(changeRate.indexOf("/") + 1, changeRate.length()));
        return (molecular / denominator) < 1;
    }

    private String selectAddress(PushSaleOrder pushSaleOrder) {
        String address = "";
        if (Constants.OUT_ORDER_SOURCE.equals(pushSaleOrder.getOrderSource())) {
            address = pushSaleOrder.getProvince() + pushSaleOrder.getCity() + pushSaleOrder.getArea() + pushSaleOrder.getReceiverAddress();
        } else {
            address = pushSaleOrder.getReceiverAddress();
        }
        return address;
    }

}
