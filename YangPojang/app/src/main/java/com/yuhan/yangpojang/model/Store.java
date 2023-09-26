package com.yuhan.yangpojang.model;

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

    public Store(double latitude, double longitude, boolean isVerified, boolean hasMeeting, String storeName, String storeAddress, String openingHours, float rating){
        this.latitude = latitude;
        this.longitude = longitude;
        this.isVerified = isVerified;
        this.hasMeeting = hasMeeting;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.openingHours = openingHours;
        this.rating = rating;
    }

    public Store(){}

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

}
