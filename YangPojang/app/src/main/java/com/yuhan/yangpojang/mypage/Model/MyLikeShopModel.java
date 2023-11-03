package com.yuhan.yangpojang.mypage.Model;

public class MyLikeShopModel {

    String shopName; //가게 이름
    String setAddressName; //가게 주소
    String category; //카테고리

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setAddressName(String add) { this.setAddressName = add; }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getShopName() {
        return shopName;
    }

    public String getAddressName() { return setAddressName; }

    public String getCategory() {
        return category;
    }
}
