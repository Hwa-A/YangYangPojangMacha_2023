package com.yuhan.yangpojang.mypage.Model;

public class MyReportShopModel {
    String shopName; //가게 이름
    String add; //가게 주소
    String category; //카테고리

    public MyReportShopModel(String shopName, String add, String category) {
        this.shopName = shopName;
        this.add = add;
        this.category = category;
    }

    public String getShopName() {
        return shopName;
    }

    public String getAdd() {
        return add;
    }

    public String getCategory() {
        return category;
    }
}
