package com.yuhan.yangpojang.model;

//Store 클래스 - 가게 정보를 저장하는 모델 클래스
public class Store {

    private String primaryKey; //기본키
    double latitude; //위도
    double longitude; //경도
    boolean verified; //인증 여부
    boolean hasMeeting; //번개 여부
    String shopName; //가게 이름
    String addressName; //가게 주소
    float rating; //가게 별점
    String category; //카테고리
    String exteriorImagePath; //가게이미지 url
    String geohash; //가게 위치에 대한 지오해쉬값

    public Store(){}

    public String getPrimaryKey() { return primaryKey; }

    public void setPrimaryKey(String primaryKey) { this.primaryKey = primaryKey; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean getHasMeeting() {
        return hasMeeting;
    }

    public void setHasMeeting(boolean hasMeeting) {
        this.hasMeeting = hasMeeting;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCategory() {return category;}

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExteriorImagePath() {
        return exteriorImagePath;
    }

    public void setExteriorImagePath(String exteriorImagePath) {
        this.exteriorImagePath = exteriorImagePath;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }
}
