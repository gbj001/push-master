package com.xinliangjishipin.pushwms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinliangjishipin.pushwms.entity.*;

import java.util.Date;
import java.util.List;

public class PushDeliveryOrder {
    private String pkOrder;
    private String pkOrg;
    private String warehouseCode;
    private String billCode;
    private String orderType;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date billDate;
    private String remark;
    private String receiverName;
    private String receiverMobile;
    private String receiverTel;
    private String receiverAddress;
    private String province;
    private String city;
    private String area;
    private String customerName;
    private String customerCode;
    private String orderSource;
    private List<DeliveryOrderDetail> items;
    private String auditTime;

    public PushDeliveryOrder() {

    }

    public PushDeliveryOrder(DeliveryOrder deliveryOrder) {
        this.pkOrder = deliveryOrder.getPkOrder();
        this.pkOrg = deliveryOrder.getPkOrg();
        this.billCode = deliveryOrder.getBillCode();
        this.billDate = deliveryOrder.getBillDate();
        this.remark = deliveryOrder.getRemark() == null ? "" : deliveryOrder.getRemark();
        this.receiverName = deliveryOrder.getReceiverName();
        this.receiverMobile = deliveryOrder.getReceiverMobile();
        this.receiverTel = deliveryOrder.getReceiverTel();
        this.receiverAddress = deliveryOrder.getReceiverAddress();
        this.province = deliveryOrder.getProvince();
        this.city = deliveryOrder.getCity();
        this.area = deliveryOrder.getArea();
        this.customerName = deliveryOrder.getCustomerName();
        this.customerCode = deliveryOrder.getCustomerCode();
        this.orderSource = deliveryOrder.getOrderSource();
    }

    public String getPkOrder() {
        return pkOrder;
    }

    public void setPkOrder(String pkOrder) {
        this.pkOrder = pkOrder;
    }

    public String getPkOrg() {
        return pkOrg;
    }

    public void setPkOrg(String pkOrg) {
        this.pkOrg = pkOrg;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverTel() {
        return receiverTel;
    }

    public void setReceiverTel(String receiverTel) {
        this.receiverTel = receiverTel;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public List<DeliveryOrderDetail> getItems() {
        return items;
    }

    public void setItems(List<DeliveryOrderDetail> items) {
        this.items = items;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }
}
