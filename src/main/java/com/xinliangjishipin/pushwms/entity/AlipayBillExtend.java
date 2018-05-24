package com.xinliangjishipin.pushwms.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class AlipayBillExtend implements Serializable {
    private String billNo;
    private String businessNo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date tradeTime;
    private String sourceName;
    private String sourceAccount;
    private String incomeAmount;
    private String businessType;
    private String importType;
    private String remark;
    private String matchStatus;
    private String matchContent;
    private String matchCustomerCode;
    private String matchCustomerName;
    private String requestStatus;
    private Date requestTime;
    private String requestText;
    private String responseStatus;
    private Date responseTime;
    private String responseText;
    private String verifyStatus;
    private Date verifyTime;
    private String verifyText;
    private String createdUser;
    private Date createdTime;
    private String updatedUser;
    private Date updatedTime;
    private String def1;
    private String def2;
    private String def3;
    private String def4;
    private String def5;
    private String def6;
    private String def7;
    private String def8;
    private String def9;
    private String def10;

    public AlipayBillExtend() {

    }

    public AlipayBillExtend(AlipayBillInfo alipayBillInfo) {
        this.billNo = alipayBillInfo.getBillNo();
        this.businessNo = alipayBillInfo.getBusinessNo();
        this.tradeTime = alipayBillInfo.getTradeTime();
        this.sourceName = alipayBillInfo.getSourceName();
        this.sourceAccount = alipayBillInfo.getSourceAccount();
        this.incomeAmount = alipayBillInfo.getIncomeAmount();
        this.businessType = alipayBillInfo.getBusinessType();
        this.remark = alipayBillInfo.getRemark();
    }

    public AlipayBillExtend(String businessNo, Date tradeTime, String sourceName, String sourceAccount, String incomeAmount, String createdUser, String importType) {
        this.billNo = String.valueOf(System.currentTimeMillis());
        this.businessNo = businessNo;
        this.tradeTime = tradeTime;
        this.sourceName = sourceName;
        this.sourceAccount = sourceAccount;
        this.incomeAmount = incomeAmount;
        this.importType = importType;
        this.remark = "";
        this.businessType = "人工";
        this.createdUser = createdUser;
        this.createdTime = new Date();
        this.updatedUser = "";
        this.updatedTime = new Date();
        this.verifyStatus = "0";
        this.verifyTime = new Date();
        this.verifyText = "";
    }

    @Override
    public String toString() {
        return "AlipayBillExtend{" +
                "billNo='" + billNo + '\'' +
                ", businessNo='" + businessNo + '\'' +
                ", tradeTime=" + tradeTime +
                ", sourceName='" + sourceName + '\'' +
                ", sourceAccount='" + sourceAccount + '\'' +
                ", incomeAmount='" + incomeAmount + '\'' +
                ", businessType='" + businessType + '\'' +
                ", importType='" + importType + '\'' +
                ", remark='" + remark + '\'' +
                ", matchStatus='" + matchStatus + '\'' +
                ", matchContent='" + matchContent + '\'' +
                ", matchCustomerCode='" + matchCustomerCode + '\'' +
                ", matchCustomerName='" + matchCustomerName + '\'' +
                ", requestStatus='" + requestStatus + '\'' +
                ", requestTime=" + requestTime +
                ", requestText='" + requestText + '\'' +
                ", responseStatus='" + responseStatus + '\'' +
                ", responseTime=" + responseTime +
                ", responseText='" + responseText + '\'' +
                ", verifyStatus='" + verifyStatus + '\'' +
                ", verifyTime=" + verifyTime +
                ", verifyText='" + verifyText + '\'' +
                ", createdUser='" + createdUser + '\'' +
                ", createdTime=" + createdTime +
                ", updatedUser='" + updatedUser + '\'' +
                ", updatedTime=" + updatedTime +
                ", def1='" + def1 + '\'' +
                ", def2='" + def2 + '\'' +
                ", def3='" + def3 + '\'' +
                ", def4='" + def4 + '\'' +
                ", def5='" + def5 + '\'' +
                ", def6='" + def6 + '\'' +
                ", def7='" + def7 + '\'' +
                ", def8='" + def8 + '\'' +
                ", def9='" + def9 + '\'' +
                ", def10='" + def10 + '\'' +
                '}';
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public String getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(String incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getMatchContent() {
        return matchContent;
    }

    public void setMatchContent(String matchContent) {
        this.matchContent = matchContent;
    }

    public String getMatchCustomerCode() {
        return matchCustomerCode;
    }

    public void setMatchCustomerCode(String matchCustomerCode) {
        this.matchCustomerCode = matchCustomerCode;
    }

    public String getMatchCustomerName() {
        return matchCustomerName;
    }

    public void setMatchCustomerName(String matchCustomerName) {
        this.matchCustomerName = matchCustomerName;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(String verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public Date getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(Date verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getVerifyText() {
        return verifyText;
    }

    public void setVerifyText(String verifyText) {
        this.verifyText = verifyText;
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

    public String getDef1() {
        return def1;
    }

    public void setDef1(String def1) {
        this.def1 = def1;
    }

    public String getDef2() {
        return def2;
    }

    public void setDef2(String def2) {
        this.def2 = def2;
    }

    public String getDef3() {
        return def3;
    }

    public void setDef3(String def3) {
        this.def3 = def3;
    }

    public String getDef4() {
        return def4;
    }

    public void setDef4(String def4) {
        this.def4 = def4;
    }

    public String getDef5() {
        return def5;
    }

    public void setDef5(String def5) {
        this.def5 = def5;
    }

    public String getDef6() {
        return def6;
    }

    public void setDef6(String def6) {
        this.def6 = def6;
    }

    public String getDef7() {
        return def7;
    }

    public void setDef7(String def7) {
        this.def7 = def7;
    }

    public String getDef8() {
        return def8;
    }

    public void setDef8(String def8) {
        this.def8 = def8;
    }

    public String getDef9() {
        return def9;
    }

    public void setDef9(String def9) {
        this.def9 = def9;
    }

    public String getDef10() {
        return def10;
    }

    public void setDef10(String def10) {
        this.def10 = def10;
    }
}
