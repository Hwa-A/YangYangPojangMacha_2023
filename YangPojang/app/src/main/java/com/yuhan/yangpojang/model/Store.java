package com.yuhan.yangpojang.model;

import com.google.firebase.firestore.GeoPoint;

//Store 클래스 - 가게 정보를 저장하는 모델 클래스
public class Store {
    double latitude; //위도
    double longitude; //경도
    boolean isVerified; //인증 여부
    boolean hasMeeting; //번개 여부
    String storeName; //가게 이름
    String storeAddress; //가게 주소
    String openingHours; //가게 영업시간
    float rating; //가게 별점
    String category; //카테고리
    String imageUrl; //가게이미지 url
    GeoPoint location; // 가게 위치
    String geohash; //가게 위치에 대한 지오해쉬값

    public Store(){}

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

    public boolean getisVerified() {
        return isVerified;
    }

    public void setisVerified(boolean verified) {
        isVerified = verified;
    }

    public boolean getHasMeeting() {
        return hasMeeting;
    }

    public void setHasMeeting(boolean hasMeeting) {
        this.hasMeeting = hasMeeting;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }
}
