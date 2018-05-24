package com.xinliangjishipin.pushwms.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gengbeijun
 */
public class OrderExchangeInfo implements Serializable {
    private String outOrderId;
    private String orderType;
    private Integer pushCount;
    private Integer pushProcessCount;
    private String pushStatus;
    private String pushChannel;
    private String createdUser;
    private Date createdTime;
    private String updatedUser;
    private Date updatedTime;
    private String requestContent;
    private String responseContent;
    private Date responseTime;


    public OrderExchangeInfo(){

    }

    public OrderExchangeInfo(PurchaseOrder purchaseOrder, String orderType){
        this.outOrderId = purchaseOrder.getBillCode().trim();
        this.orderType = orderType;
        this.pushCount = 0;
        this.pushProcessCount = 0;
        this.pushStatus = "0";
        this.createdUser = "system";
        this.createdTime = new Date();
        this.updatedUser = "";
        this.updatedTime = new Date();
        this.requestContent = "";
        this.responseTime = new Date();
        this.responseContent = "";

    }

    public OrderExchangeInfo(SaleOrder saleOrder, String orderType){
        this.outOrderId = saleOrder.getBillCode();
        this.orderType = orderType;
        this.pushCount = 0;
        this.pushProcessCount = 0;
        this.pushStatus = "0";
        this.createdUser = "system";
        this.createdTime = new Date();
        this.updatedUser = "";
        this.updatedTime = new Date();
        this.requestContent = "";
        this.responseTime = new Date();
        this.responseContent = "";

    }

    public OrderExchangeInfo(DeliveryOrder deliveryOrder, String orderType){
        this.outOrderId = deliveryOrder.getBillCode();
        this.orderType = orderType;
        this.pushCount = 0;
        this.pushProcessCount = 0;
        this.pushStatus = "0";
        this.createdUser = "system";
        this.createdTime = new Date();
        this.updatedUser = "";
        this.updatedTime = new Date();
        this.requestContent = "";
        this.responseTime = new Date();
        this.responseContent = "";
    }

    @Override
    public String toString() {
        return "OrderExchangeInfo{" +
                "outOrderId='" + outOrderId + '\'' +
                ", orderType='" + orderType + '\'' +
                ", pushCount=" + pushCount +
                ", pushProcessCount=" + pushProcessCount +
                ", pushStatus='" + pushStatus + '\'' +
                ", createdUser='" + createdUser + '\'' +
                ", createdTime=" + createdTime +
                ", updatedUser='" + updatedUser + '\'' +
                ", updatedTime=" + updatedTime +
                ", requestContent='" + requestContent + '\'' +
                '}';
    }

    public String getOutOrderId() {
        return outOrderId;
    }

    public void setOutOrderId(String outOrderId) {
        this.outOrderId = outOrderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getPushCount() {
        return pushCount;
    }

    public void setPushCount(Integer pushCount) {
        this.pushCount = pushCount;
    }

    public Integer getPushProcessCount() {
        return pushProcessCount;
    }

    public void setPushProcessCount(Integer pushProcessCount) {
        this.pushProcessCount = pushProcessCount;
    }

    public String getPushStatus() {
        return pushStatus;
    }

    public void setPushStatus(String pushStatus) {
        this.pushStatus = pushStatus;
    }

    public String getPushChannel() {
        return pushChannel;
    }

    public void setPushChannel(String pushChannel) {
        this.pushChannel = pushChannel;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }
}
