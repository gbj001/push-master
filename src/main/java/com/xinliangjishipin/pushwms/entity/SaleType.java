package com.xinliangjishipin.pushwms.entity;

public enum SaleType {
    GENERAL("30-01", "4C-01"), BACK("30-02", "4C-01"), DELIVERY("4331-01", "4C-01"), TASTE("30-Cxx-001", "4C-Cxx-001");
    // 成员变量
    private String inType;
    private String outType ;
    // 构造方法
    private SaleType(String inType, String outType) {
        this.inType = inType;
        this.outType = outType;
    }
    // 普通方法
    public static String getOutTypeByInType(String inType) {
        for (SaleType c : SaleType.values()) {
            if (c.getInType().equals(inType)) {
                return c.getOutType();
            }
        }
        return "";
    }

    public String getInType() {
        return inType;
    }

    public void setInType(String inType) {
        this.inType = inType;
    }

    public String getOutType() {
        return outType;
    }

    public void setOutType(String outType) {
        this.outType = outType;
    }


    public static void main(String args[]){
        System.out.println(SaleType.getOutTypeByInType("30-Cxx-001"));
    }
}
