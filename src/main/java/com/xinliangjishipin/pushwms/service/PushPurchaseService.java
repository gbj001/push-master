package com.xinliangjishipin.pushwms.service;

import com.xinliangjishipin.pushwms.dto.PushPurchaseOrder;
import com.xinliangjishipin.pushwms.entity.OrderExchangeInfo;
import com.xinliangjishipin.pushwms.entity.PurchaseOrder;
import com.xinliangjishipin.pushwms.entity.PurchaseOrderDetail;
import com.xinliangjishipin.pushwms.entity.WarehouseConfig;
import com.xinliangjishipin.pushwms.mapper.OrderExchangeInfoMapper;
import com.xinliangjishipin.pushwms.mapper.PurchaseOrderDetailMapper;
import com.xinliangjishipin.pushwms.mapper.PurchaseOrderMapper;
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

/**
 * @author gengbeijun
 */
@Service
public class PushPurchaseService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderExchangeInfoMapper orderExchangeInfoMapper;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private PurchaseOrderDetailMapper purchaseOrderDetailMapper;

    @Autowired
    private WarehouseConfigMapper warehouseConfigMapper;

    @Autowired
    private RemoteClientService remoteClient;

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

    @Value("${wms.wj.url}")
    private String wjUrl;

    @Value("${wms.wj.appId}")
    private String appId;

    @Value("${wms.wj.secretKey}")
    private String secretKey;

    @Value("${wms.wj.companyCode}")
    private String wjCompanyCode;

    @Value("${wms.msp.system}")
    private String wmsMSPSystem;

    @Value("${wms.msp.url}")
    private String mspUrl;

    @Value("${wms.fl.system}")
    private String wmsFLSystem;

    @Value("${wms.fl.customerId}")
    private String wmsFLCustomerID;

    @Value("${wms.fl.url}")
    private String flUrl;

    @Value("${wms.fl.appsecrt}")
    private String flAppsecrt;

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
     * 采购订单推送
     */
    public void pushOrder(List<PushPurchaseOrder> resultPOrder) {
        for (PushPurchaseOrder pushPurchaseOrder : resultPOrder) {
            //查询第三方wms配置表
            WarehouseConfig warehouseConfig = warehouseConfigMapper.getByNcWarehouseCode(pushPurchaseOrder.getWarehouseCode().toUpperCase());
            if (warehouseConfig == null) {
                OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(pushPurchaseOrder.getBillCode());
                logger.info("仓库编码不存在对应wms系统仓库编码,采购单号为：" + pushPurchaseOrder.getBillCode() + "，对应仓库编码为：" + pushPurchaseOrder.getWarehouseCode());
                orderExchangeInfo.setRequestContent("仓库编码不存在对应wms系统仓库编码,采购单号为：" + pushPurchaseOrder.getBillCode() + "，对应仓库编码为：" + pushPurchaseOrder.getWarehouseCode());
                orderExchangeInfo.setPushChannel("");
                orderExchangeInfo.setUpdatedUser("");
                orderExchangeInfoMapper.updateOrderExchangeInfo(orderExchangeInfo);
                continue;
            }
            OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(pushPurchaseOrder.getBillCode());

            String pushStartTime = warehouseConfig.getStartTime();
            String pushEndTime = warehouseConfig.getEndTime();
            if (warehouseConfig.getWmsShort().equals(wmsWJSystem) && DateUtil.dateInStartTimeAndEndTime(pushPurchaseOrder.getAuditTime(), pushStartTime, pushEndTime, DATE_FORMAT)) {
                //转换成wj需要的json格式数据
                String json = convertToWJJson(pushPurchaseOrder, warehouseConfig.getWmsWarehouseCode());
                remoteClient.remoteClient(orderExchangeInfo, wjUrl, json, wmsWJSystem, jsonTransData);
                orderExchangeInfo.setPushChannel(wmsWJSystem);
            }
            if (warehouseConfig.getWmsShort().equals(wmsMSPSystem) && DateUtil.dateInStartTimeAndEndTime(pushPurchaseOrder.getAuditTime(), pushStartTime, pushEndTime, DATE_FORMAT)) {
                //转换成wj需要的json格式数据
                String json = convertToMSPJson(pushPurchaseOrder, warehouseConfig.getWmsWarehouseCode());
                remoteClient.remoteClient(orderExchangeInfo, mspUrl, json, wmsMSPSystem, jsonTransData);
                orderExchangeInfo.setPushChannel(wmsMSPSystem);
            }
            if (warehouseConfig.getWmsShort().equals(wmsFLSystem) && DateUtil.dateInStartTimeAndEndTime(pushPurchaseOrder.getAuditTime(), pushStartTime, pushEndTime, DATE_FORMAT)) {
                //转换成fl需要的xml格式数据
                String xmlData = convertToFLXml(pushPurchaseOrder, warehouseConfig.getWmsWarehouseCode());
                String secretXmlData = flAppsecrt + xmlData + flAppsecrt;
                String sign = "";
                try {
                    sign = URLEncoder.encode(Base64Util.getBase64(MD5EncoderUtil.MD5Encoder(secretXmlData)).toUpperCase(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                remoteClient.remoteXmlClient(orderExchangeInfo, flUrl, sign, xmlData, Constants.FL_PURCHASE_ORDER_TYPE);
                orderExchangeInfo.setPushChannel(wmsFLSystem);
            }
            orderExchangeInfo.setUpdatedUser("");
            orderExchangeInfo.setUpdatedTime(new Date());
            orderExchangeInfoMapper.updateOrderExchangeInfo(orderExchangeInfo);
        }
    }

    /**
     * 从NC获取需要推送的采购订单
     */
    public List<PushPurchaseOrder> outputPushPoOrder() {
        List<PushPurchaseOrder> resultPOrder = new ArrayList<PushPurchaseOrder>();

        //一. 查询已审核的采购单主表
        List<PurchaseOrder> purchaseOrderList = purchaseOrderMapper.getPurchaseOrders();
        for (PurchaseOrder purchaseOrder : purchaseOrderList) {
            OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(purchaseOrder.getBillCode());
            //1、过滤掉已经推送成功的采购订单
            boolean orderExchangeInfoIsNull = orderExchangeInfo == null;
            boolean pushStatus = (orderExchangeInfo != null && orderExchangeInfo.getPushStatus().equals(pushOrderFail) && orderExchangeInfo.getPushCount() < pushOrderMaxCount);
            boolean isChangeAuditTime = (orderExchangeInfo != null && DateUtil.compare_date(purchaseOrder.getAuditTime(), fmt.format(orderExchangeInfo.getUpdatedTime()), DATE_FORMAT) ==1 );
            boolean flag =  orderExchangeInfoIsNull || pushStatus || isChangeAuditTime;
            if (flag) {
                PushPurchaseOrder pushPurchaseOrder = new PushPurchaseOrder(purchaseOrder);
                orderExchangeInfoService.updateOrderExchangeInfo(orderExchangeInfo, purchaseOrder, null, null);
                //根据采购订单主ID获取采购订单明细
                List<PurchaseOrderDetail> allPurchaseOrderDetailList = purchaseOrderDetailMapper.getPurchaseOrderDetail(purchaseOrder.getPkOrder());

                //过滤掉订单明细中包含多个仓库的采购单
                if (!isSync(allPurchaseOrderDetailList, purchaseOrder.getBillCode())) {
                    continue;
                }

                List<PurchaseOrderDetail> pushPoOrderDetailList = new ArrayList<>();
                for (PurchaseOrderDetail purchaseOrderDetail : allPurchaseOrderDetailList) {
                    purchaseOrderDetail.setBillCode(purchaseOrder.getBillCode());
                    purchaseOrderDetail.setPkOrderHeader(purchaseOrder.getPkOrder());
                    pushPoOrderDetailList.add(purchaseOrderDetail);
                }

                pushPurchaseOrder.setWarehouseCode(allPurchaseOrderDetailList.get(0).getWarehouseCode());
                pushPurchaseOrder.setItems(pushPoOrderDetailList);
                pushPurchaseOrder.setAuditTime(purchaseOrder.getAuditTime());
                resultPOrder.add(pushPurchaseOrder);
            }
        }

        return resultPOrder;
    }

    /**
     * 转换成唯捷需要的json格式
     */
    public String convertToWJJson(PushPurchaseOrder pushPurchaseOrder, String wmswarehouseCode) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder bizContent = new StringBuilder();
        stringBuilder.append("{\"action\": \"weijie.wms.purchase.add\",\"appId\":\"").append(appId).append("\",");

        bizContent.append("{\\\"purchaseOrderDate\\\":\\\"").append(fmt.format(pushPurchaseOrder.getdMakeDate())).append("\\\",\\\"goodsSupplierCode\\\": \\\"").append(pushPurchaseOrder.getPkSupplierCode()).append("\\\",\\\"goodsSupplierName\\\": \\\"").append(pushPurchaseOrder.getPkSupplierName()).append("\\\",\\\"upstreamNumber\\\": \\\"").append(pushPurchaseOrder.getBillCode()).append("\\\", \\\"transportNumber\\\": \\\"\\\", \\\"contractNumber\\\": \\\"\\\", \\\"areaCode\\\": \\\"\\\", \\\"provinceCode\\\": \\\"\\\", \\\"cityCode\\\": \\\"\\\", \\\"countyCode\\\": \\\"\\\", \\\"province\\\": \\\"\\\", \\\"city\\\": \\\"\\\", \\\"county\\\": \\\"\\\", \\\"wjWarehouseCode\\\":\\\"").append(wmswarehouseCode).append("\\\", \\\"wjCompanyCode\\\": \\\"").append(wjCompanyCode).append("\\\", \\\"remark\\\": \\\"\\\",");
        bizContent.append("\\\"goods\\\": [");
        for (PurchaseOrderDetail purchaseOrderDetail : pushPurchaseOrder.getItems()) {
            boolean flag = this.isTransferChangeRate(purchaseOrderDetail.getTransRate());
            double goodsCount = flag ? purchaseOrderDetail.getQuantity() : purchaseOrderDetail.getMainQuantity();
            String goodsUnit = flag ? purchaseOrderDetail.getQuantityUnits() : purchaseOrderDetail.getMainQuantityUnits();
            bizContent.append("{\\\"goodsNo\\\": \\\"").append(purchaseOrderDetail.getMaterialCode())
                    .append("\\\",\\\"batchNo\\\": \\\"\\\",\\\"goodsCount\\\": \\\"")
                    .append(goodsCount)
                    .append("\\\",\\\"goodsUnit\\\": \\\"")
                    .append(goodsUnit)
                    .append("\\\",\\\"changeRate\\\": \\\"")
                    .append(purchaseOrderDetail.getTransRate())
                    .append("\\\",\\\"isGift\\\": \\\"")
                    .append(purchaseOrderDetail.getIsGift())
                    .append("\\\", \\\"goodsPrice\\\": \\\"0\\\",\\\"goodsTotalPrice\\\": \\\"\\\",\\\"goodsWeight\\\": \\\"\\\",\\\"goodsSize\\\": \\\"\\\",\\\"goodsPromoteType\\\": \\\"")
                    .append(purchaseOrderDetail.getIsGift()).append("\\\",\\\"extendInfo\\\":{\\\"ctrantypeid\\\":\\\"")
                    .append(cTranType).append("\\\",\\\"csourcetype\\\":\\\"21\\\",\\\"csourcebillhid\\\":\\\"")
                    .append(purchaseOrderDetail.getPkOrderHeader()).append("\\\",\\\"csourcebillbid\\\": \\\"")
                    .append(purchaseOrderDetail.getPkOrderBody()).append("\\\", \\\"vsourcebillcode\\\": \\\"")
                    .append(pushPurchaseOrder.getBillCode()).append("\\\"}, \\\"remark\\\": \\\"\\\"},");
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
    public String convertToMSPJson(PushPurchaseOrder pushPurchaseOrder, String wmsWarehouseCode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"order\": {\"warehouse_number\":\"").append(wmsWarehouseCode).append("\",\"ctrantypeid\": \"").append(cTranType).append("\",\"customer_number\": \"").append(pushPurchaseOrder.getBillCode()).append("\",\"delivery_address\": \"\",\"sign_up\": \"\",\"sign_up_tel\": \"\",\"remarks\": \"\"},");
        stringBuilder.append("\"goods\": [");
        for (PurchaseOrderDetail purchaseOrderDetail : pushPurchaseOrder.getItems()) {
            boolean flag = this.isTransferChangeRate(purchaseOrderDetail.getTransRate());
            double goodsCount = flag ? purchaseOrderDetail.getQuantity() : purchaseOrderDetail.getMainQuantity();
            stringBuilder.append("{\"goods_name\": \"").append(purchaseOrderDetail.getMaterialName())
                    .append("\",\"goods_number\": \"").append(purchaseOrderDetail.getMaterialCode())
                    .append("\",\"gifts\": \"").append(purchaseOrderDetail.getIsGift())
                    .append("\",\"specification\": \"\",\"barcode\": \"\",\"color\": \"\",\"style\": \"\",\"weight\": \"\",\"volume\": \"\",\"small_unit_quantity\": \"")
                    .append(goodsCount)
                    .append("\",\"trans_rate\": \"").append(purchaseOrderDetail.getTransRate())
                    .append("\",\"cost\": \"\",\"temperature\": \"\",\"humidity\": \"\",\"csourcetype\": \"21\",\"csourcebillhid\": \"")
                    .append(purchaseOrderDetail.getPkOrderHeader())
                    .append("\",\"csourcebillbid\": \"").append(purchaseOrderDetail.getPkOrderBody())
                    .append("\",\"vsourcebillcode\": \"").append(pushPurchaseOrder.getBillCode())
                    .append("\",\"remark\": \"\"},");
        }
        stringBuilder = stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());

        stringBuilder.append("],");

        stringBuilder.append("\"supply\": {\"name\": \"").append(pushPurchaseOrder.getPkSupplierName())
                .append("\",\"code\": \"").append(pushPurchaseOrder.getPkSupplierCode()).append("\"}");
        stringBuilder.append("}");
        return stringBuilder.toString();

    }


    public String convertToFLXml(PushPurchaseOrder pushPurchaseOrder, String wmsWarehouseCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xmldata><header>");
        sb.append("<OrderNo>").append(pushPurchaseOrder.getBillCode()).append("</OrderNo>");
        sb.append("<OrderType>").append("1").append("</OrderType>");
        sb.append("<CustomerID>").append(wmsFLCustomerID).append("</CustomerID>");
        sb.append("<Notes>").append("").append("</Notes>");
        sb.append("<SupplierID>").append(pushPurchaseOrder.getPkSupplierCode()).append("</SupplierID>");
        sb.append("<Supplier_Name>").append(pushPurchaseOrder.getPkSupplierName()).append("</Supplier_Name>");
        sb.append("<WarehouseID>").append(wmsWarehouseCode).append("</WarehouseID>");
        sb.append("<H_EDI_03>").append(cTranType).append("</H_EDI_03>");
        for (PurchaseOrderDetail purchaseOrderDetail : pushPurchaseOrder.getItems()) {
            boolean flag = this.isTransferChangeRate(purchaseOrderDetail.getTransRate());
            sb.append("<detailsItem>");
            sb.append("<LineNo>").append(purchaseOrderDetail.getCrowNo()).append("</LineNo>");
            sb.append("<CustomerID>").append(wmsFLCustomerID).append("</CustomerID>");
            sb.append("<SKU>").append(purchaseOrderDetail.getMaterialCode()).append("</SKU>");
            sb.append("<ExpectedQty>").append(flag ? purchaseOrderDetail.getQuantity() : purchaseOrderDetail.getMainQuantity()).append("</ExpectedQty>");
            sb.append("<Notes>").append("").append("</Notes>");
            sb.append("<D_EDI_03>").append(purchaseOrderDetail.getPkOrderBody()).append("</D_EDI_03>");
            sb.append("<D_EDI_04>").append(purchaseOrderDetail.getIsGift()).append("</D_EDI_04>");
            sb.append("<D_EDI_05>").append("21").append("</D_EDI_05>");
            sb.append("<D_EDI_06>").append(purchaseOrderDetail.getBillCode()).append("</D_EDI_06>");
            sb.append("<D_EDI_07>").append(purchaseOrderDetail.getPkOrderHeader()).append("</D_EDI_07>");
            sb.append("<D_EDI_08>").append(purchaseOrderDetail.getTransRate()).append("</D_EDI_08>");
            sb.append("</detailsItem>");
        }
        sb.append("</header></xmldata>");
        return sb.toString();
    }

    /**
     * 过滤仓库编码不一致的采购订单
     */
    public boolean isSync(List<PurchaseOrderDetail> purchaseOrderDetailList, String pkOrderId) {
        boolean flag = false;
        Set<String> warehouseCodeSet = new HashSet<>();
        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetailList) {
            warehouseCodeSet.add(purchaseOrderDetail.getWarehouseCode());
        }
        if (warehouseCodeSet.size() == 1) {
            flag = true;
        } else {
            logger.info("采购订单明细中仓库编码大于1个：" + pkOrderId);
        }
        return flag;
    }

    private boolean isTransferChangeRate(String changeRate) {
        double molecular = Double.parseDouble(changeRate.substring(0, changeRate.indexOf("/")));
        double denominator = Double.parseDouble(changeRate.substring(changeRate.indexOf("/") + 1, changeRate.length()));
        return (molecular / denominator) < 1;
    }
}
