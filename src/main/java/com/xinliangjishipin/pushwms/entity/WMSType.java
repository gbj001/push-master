package com.xinliangjishipin.pushwms.entity;

public enum WMSType {
    WJ("WJ", "唯捷WMS系统深圳仓"), MSP("MSP", "码上配WMS系统上海仓"), FL("FL", "新联道WMS系统北京仓");
    private String wmsCode;
    private String wmsName;

    private WMSType(String wmsCode, String wmsName) {
        this.wmsCode = wmsCode;
        this.wmsName = wmsName;
    }

    public static String getNameByCode(String wmsCode) {
        for (WMSType c : WMSType.values()) {
            if (c.getWmsCode().equals(wmsCode)) {
                return c.getWmsName();
            }
        }
        return "";
    }

    public String getWmsCode() {
        return wmsCode;
    }

    public void setWmsCode(String wmsCode) {
        this.wmsCode = wmsCode;
    }

    public String getWmsName() {
        return wmsName;
    }

    public void setWmsName(String wmsName) {
        this.wmsName = wmsName;
    }
}
