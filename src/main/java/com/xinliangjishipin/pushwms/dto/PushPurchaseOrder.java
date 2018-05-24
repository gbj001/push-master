package com.xinliangjishipin.pushwms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinliangjishipin.pushwms.entity.PurchaseOrder;
import com.xinliangjishipin.pushwms.entity.PurchaseOrderDetail;

import java.util.Date;
import java.util.List;

/**
 * @author gengbeijun
 * 向wms推送采购单实体
 */
public class PushPurchaseOrder {
    private String pkOrder;
    private String billCode;
    private String warehouseCode;
    private String ownerId;
    private String pkSupplierName;
    private String pkSupplierCode;
    private List<PurchaseOrderDetail> items;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dMakeDate;
    private String vTranTypeCode;
    private String bReturn;
    private String auditTime;

    public PushPurchaseOrder(){

    }

    public PushPurchaseOrder(PurchaseOrder purchaseOrder){
        this.pkOrder = purchaseOrder.getPkOrder();
        this.billCode = purchaseOrder.getBillCode();
        this.ownerId = purchaseOrder.getOwnerId();
        this.pkSupplierName = purchaseOrder.getPkSupplierName();
        this.pkSupplierCode = purchaseOrder.getPkSupplierCode();
        this.dMakeDate = purchaseOrder.getdMakeDate();
        this.vTranTypeCode = purchaseOrder.getvTranTypeCode();
    }

    public String getPkOrder() {
        return pkOrder;
    }

    public void setPkOrder(String pkOrder) {
        this.pkOrder = pkOrder;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPkSupplierName() {
        return pkSupplierName;
    }

    public void setPkSupplierName(String pkSupplierName) {
        this.pkSupplierName = pkSupplierName;
    }

    public String getPkSupplierCode() {
        return pkSupplierCode;
    }

    public void setPkSupplierCode(String pkSupplierCode) {
        this.pkSupplierCode = pkSupplierCode;
    }

    public List<PurchaseOrderDetail> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderDetail> items) {
        this.items = items;
    }

    public Date getdMakeDate() {
        return dMakeDate;
    }

    public void setdMakeDate(Date dMakeDate) {
        this.dMakeDate = dMakeDate;
    }

    public String getvTranTypeCode() {
        return vTranTypeCode;
    }

    public void setvTranTypeCode(String vTranTypeCode) {
        this.vTranTypeCode = vTranTypeCode;
    }

    public String getbReturn() {
        return bReturn;
    }

    public void setbReturn(String bReturn) {
        this.bReturn = bReturn;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }
}
