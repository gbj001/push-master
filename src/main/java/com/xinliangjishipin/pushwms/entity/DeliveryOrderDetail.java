package com.xinliangjishipin.pushwms.entity;

import java.io.Serializable;

public class DeliveryOrderDetail implements Serializable {
    private String isGift;
    private String warehouseCode;
    private String materialCode;
    private String materialName;
    private String materialSpec;
    private Double quantity;
    private String quantityUnits;
    private Double mainQuantity;
    private String mainQuantityUnits;
    private String transRate;
    private String pkOrderHeader;
    private String pkOrderBody;
    private String billCode;
    private String crowNo;

    public String getIsGift() {
        return isGift;
    }

    public void setIsGift(String isGift) {
        this.isGift = isGift;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialSpec() {
        return materialSpec;
    }

    public void setMaterialSpec(String materialSpec) {
        this.materialSpec = materialSpec;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getQuantityUnits() {
        return quantityUnits;
    }

    public void setQuantityUnits(String quantityUnits) {
        this.quantityUnits = quantityUnits;
    }

    public Double getMainQuantity() {
        return mainQuantity;
    }

    public void setMainQuantity(Double mainQuantity) {
        this.mainQuantity = mainQuantity;
    }

    public String getMainQuantityUnits() {
        return mainQuantityUnits;
    }

    public void setMainQuantityUnits(String mainQuantityUnits) {
        this.mainQuantityUnits = mainQuantityUnits;
    }

    public String getTransRate() {
        return transRate;
    }

    public void setTransRate(String transRate) {
        this.transRate = transRate;
    }

    public String getPkOrderHeader() {
        return pkOrderHeader;
    }

    public void setPkOrderHeader(String pkOrderHeader) {
        this.pkOrderHeader = pkOrderHeader;
    }

    public String getPkOrderBody() {
        return pkOrderBody;
    }

    public void setPkOrderBody(String pkOrderBody) {
        this.pkOrderBody = pkOrderBody;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getCrowNo() {
        return crowNo;
    }

    public void setCrowNo(String crowNo) {
        this.crowNo = crowNo;
    }
}
