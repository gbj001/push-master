package com.xinliangjishipin.pushwms.entity;

public enum ExchangeInfoOrderType {
    PURCHASE("1", "普通采购"), SALES("2", "普通销售"), SALESTASTE("3", "销售试吃"), SALSEBACK("4","退换货"), DELIVERY("5","发货单");
    private String code;
    private String name;

    private ExchangeInfoOrderType(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(String code) {
        for (ExchangeInfoOrderType c : ExchangeInfoOrderType.values()) {
            if (c.code.equals(code)) {
                return c.getName();
            }
        }
        return "";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
