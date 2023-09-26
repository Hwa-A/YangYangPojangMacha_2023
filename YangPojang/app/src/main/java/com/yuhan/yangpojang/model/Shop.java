package com.yuhan.yangpojang.model;

import java.io.Serializable;

public class Shop implements Serializable {
    private double latitude; // x 좌표
    private double longitude; // y 좌표
    private String shopName;
    private boolean pwayMobile;
    private boolean pwayCard;
    private boolean pwayAccount;
    private boolean pwayCash;
    private boolean openMon;
    private boolean openTue;
    private boolean openWed;
    private boolean openThu;
    private boolean openFri;
    private boolean openSat;
    private boolean openSun;
    private String exteriorImageUrl;
    private String menuImageUrl;
    private String addressName;

    private String category;

    //중요" firebase 이용을 위해서는 기본생성자 필수
    public Shop() {

    }


    public Shop(String shopName,double latitude, double longitude,String  addressName, boolean pwayMobile, boolean pwayCard,
                boolean pwayAccount, boolean pwayCash, boolean openMon, boolean openTue,
                boolean openWed, boolean openThu, boolean openFri, boolean openSat,
                boolean openSun, String category ,String exteriorImageUrl, String menuImageUrl) {
        this.shopName=shopName;
        this.latitude = latitude;
        this.longitude= longitude;
        this.addressName = addressName;
        this.pwayMobile = pwayMobile;
        this.pwayCard = pwayCard;
        this.pwayAccount = pwayAccount;
        this.pwayCash = pwayCash;
        this.openMon = openMon;
        this.openTue = openTue;
        this.openWed = openWed;
        this.openThu = openThu;
        this.openFri = openFri;
        this.openSat = openSat;
        this.openSun = openSun;
        this.category = category;
        this.exteriorImageUrl = exteriorImageUrl;
        this.menuImageUrl = menuImageUrl;

    }


    // Getters and Setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public boolean isPwayMobile() {
        return pwayMobile;
    }

    public void setPwayMobile(boolean pwayMobile) {
        this.pwayMobile = pwayMobile;
    }

    public boolean isPwayCard() {
        return pwayCard;
    }

    public void setPwayCard(boolean pwayCard) {
        this.pwayCard = pwayCard;
    }

    public boolean isPwayAccount() {
        return pwayAccount;
    }

    public void setPwayAccount(boolean pwayAccount) {
        this.pwayAccount = pwayAccount;
    }

    public boolean isPwayCash() {
        return pwayCash;
    }

    public void setPwayCash(boolean pwayCash) {
        this.pwayCash = pwayCash;
    }

    public boolean isOpenMon() {
        return openMon;
    }

    public void setOpenMon(boolean openMon) {
        this.openMon = openMon;
    }

    public boolean isOpenTue() {
        return openTue;
    }

    public void setOpenTue(boolean openTue) {
        this.openTue = openTue;
    }

    public boolean isOpenWed() {
        return openWed;
    }

    public void setOpenWed(boolean openWed) {
        this.openWed = openWed;
    }

    public boolean isOpenThu() {
        return openThu;
    }

    public void setOpenThu(boolean openThu) {
        this.openThu = openThu;
    }

    public boolean isOpenFri() {
        return openFri;
    }

    public void setOpenFri(boolean openFri) {
        this.openFri = openFri;
    }

    public boolean isOpenSat() {
        return openSat;
    }

    public void setOpenSat(boolean openSat) {
        this.openSat = openSat;
    }

    public boolean isOpenSun() {
        return openSun;
    }

    public void setOpenSun(boolean openSun) {
        this.openSun = openSun;
    }

    public String getExteriorImageUrl() {
        return exteriorImageUrl;
    }

    public void setExteriorImageUrl(String exteriorImageUrl) {
        this.exteriorImageUrl = exteriorImageUrl;
    }

    public String getMenuImageUrl() {
        return menuImageUrl;
    }

    public void setMenuImageUrl(String menuImageUrl) {
        this.menuImageUrl = menuImageUrl;
    }
    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }
    public String getAddressName() {return addressName;  }

    public void setAddressName(String addressName) {this.addressName = addressName;}
}