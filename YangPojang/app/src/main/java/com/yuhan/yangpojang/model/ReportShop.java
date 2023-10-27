package com.yuhan.yangpojang.model;

public class ReportShop {
    private String uid;
    private String shopKey;

    public ReportShop(){

    }
    public ReportShop(String uid)
    {
//        this.shopKey=shopKey;
        this.uid=uid;

    }
    public String getShopKey() {
        return shopKey;
    }

    public String getUid() {
        return uid;
    }

    public void setShopKey(String shopKey) {
        this.shopKey = shopKey;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
