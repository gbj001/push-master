package com.xinliangjishipin.pushwms.entity;

import java.io.Serializable;

public class WarehouseConfig implements Serializable{

  private String wmsName;
  private String wmsShort;
  private String wmsWarehouseCode;
  private String ncWarehouseCode;
  private String ncWarehouseName;
  private String flag;
  private String startTime;
  private String endTime;

    public String getWmsName() {
        return wmsName;
    }

    public void setWmsName(String wmsName) {
        this.wmsName = wmsName;
    }

    public String getWmsShort() {
        return wmsShort;
    }

    public void setWmsShort(String wmsShort) {
        this.wmsShort = wmsShort;
    }

    public String getWmsWarehouseCode() {
        return wmsWarehouseCode;
    }

    public void setWmsWarehouseCode(String wmsWarehouseCode) {
        this.wmsWarehouseCode = wmsWarehouseCode;
    }

    public String getNcWarehouseCode() {
        return ncWarehouseCode;
    }

    public void setNcWarehouseCode(String ncWarehouseCode) {
        this.ncWarehouseCode = ncWarehouseCode;
    }

    public String getNcWarehouseName() {
        return ncWarehouseName;
    }

    public void setNcWarehouseName(String ncWarehouseName) {
        this.ncWarehouseName = ncWarehouseName;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
