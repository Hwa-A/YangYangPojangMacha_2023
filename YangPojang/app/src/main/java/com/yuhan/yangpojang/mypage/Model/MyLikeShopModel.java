package com.yuhan.yangpojang.mypage.Model;

import com.yuhan.yangpojang.model.Shop;


// Shop을 상속 받아 Adapter에서 강제적 형변환을 하지 않아도 괜찮다.
public class MyLikeShopModel extends Shop {
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
    private String storeImageUri; //[content://media/external/images/media/~] 형태의 주소
    private String menuImageUri;   //[content://media/external/images/media/~] 형태의 주소 ( 외부에서 가져온 이미지라는 뜻)
    private String addressName;
    private String category;
    private String hash;
    private String fbStoreImgurl; // 파이어베이스에 들어간 이미지 url [https://firebasestorage.googleapis.com///] 형태
    private String fbMenuImgurl;  // 파이어베이스에 들어간 이미지 url [https://firebasestorage.googleapis.com///] 형태

    private boolean verified; //인증 여부
    private boolean hasMeeting; //번개 여부
    private float rating; //가게 별점
    private String geohash;
    private String exteriorImagePath;
    private String primaryKey;
    private String shopKey;
    private String uid;


    //중요" firebase 이용을 위해서는 기본생성자 필수
    public MyLikeShopModel() {
    }

    public MyLikeShopModel(String uid, String shopName, double latitude, double longitude , String  addressName, boolean pwayMobile, boolean pwayCard,
                boolean pwayAccount, boolean pwayCash, boolean openMon, boolean openTue,
                boolean openWed, boolean openThu, boolean openFri, boolean openSat,
                boolean openSun, String category , String storeImageUri, String menuImageUri,
                boolean verified, boolean hasMeeting, float rating, String geohash, String exteriorImagePath , String primaryKey, String shopKey) {


        this.uid= uid;
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
        this.storeImageUri = storeImageUri;
        this.menuImageUri = menuImageUri;
        this.verified = verified;
        this.hasMeeting =hasMeeting;
        this.exteriorImagePath = exteriorImagePath;
        this.primaryKey = primaryKey;
        this.shopKey = shopKey;

        this.rating= rating;
        this.geohash = geohash;
    }

    public String getUid() {  return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getShopName() {
        return shopName;
    }

    public boolean isPwayMobile() {
        return pwayMobile;
    }

    public boolean isPwayCard() {
        return pwayCard;
    }

    public boolean isPwayAccount() {
        return pwayAccount;
    }

    public boolean isPwayCash() {
        return pwayCash;
    }

    public boolean isOpenMon() {
        return openMon;
    }

    public boolean isOpenTue() {
        return openTue;
    }

    public boolean isOpenWed() {
        return openWed;
    }

    public boolean isOpenThu() {
        return openThu;
    }

    public boolean isOpenFri() {
        return openFri;
    }

    public boolean isOpenSat() {
        return openSat;
    }

    public boolean isOpenSun() {
        return openSun;
    }

    public String getStoreImageUri() {
        return storeImageUri;
    }

    public String getMenuImageUri() {
        return menuImageUri;
    }

    public String getAddressName() {
        return addressName;
    }

    public String getCategory() {
        return category;
    }

    public String getHash() {
        return hash;
    }

    public String getFbStoreImgurl() {
        return fbStoreImgurl;
    }

    public String getFbMenuImgurl() {
        return fbMenuImgurl;
    }

    public boolean getVerified() {
        return verified;
    }

    public boolean getHasMeeting() {
        return hasMeeting;
    }

    public float getRating() {
        return rating;
    }

    public String getGeohash() {
        return geohash;
    }
    public String getExteriorImagePath() {
        return exteriorImagePath;
    }
    public String getPrimaryKey() { return primaryKey; }
    public String getShopKey() { return shopKey;    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setPwayMobile(boolean pwayMobile) {
        this.pwayMobile = pwayMobile;
    }

    public void setPwayCard(boolean pwayCard) {
        this.pwayCard = pwayCard;
    }

    public void setPwayAccount(boolean pwayAccount) {
        this.pwayAccount = pwayAccount;
    }

    public void setPwayCash(boolean pwayCash) {
        this.pwayCash = pwayCash;
    }

    public void setOpenMon(boolean openMon) {
        this.openMon = openMon;
    }

    public void setOpenTue(boolean openTue) {
        this.openTue = openTue;
    }

    public void setOpenWed(boolean openWed) {
        this.openWed = openWed;
    }

    public void setOpenThu(boolean openThu) {
        this.openThu = openThu;
    }

    public void setOpenFri(boolean openFri) {
        this.openFri = openFri;
    }

    public void setOpenSat(boolean openSat) {
        this.openSat = openSat;
    }

    public void setOpenSun(boolean openSun) {
        this.openSun = openSun;
    }

    public void setStoreImageUri(String storeImageUri) {
        this.storeImageUri = storeImageUri;
    }

    public void setMenuImageUri(String menuImageUri) {
        this.menuImageUri = menuImageUri;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setFbStoreImgurl(String fbStoreImgurl) {
        this.fbStoreImgurl = fbStoreImgurl;
    }

    public void setFbMenuImgurl(String fbMenuImgurl) {
        this.fbMenuImgurl = fbMenuImgurl;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setHasMeeting(boolean hasMeeting) {
        this.hasMeeting = hasMeeting;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public void setExteriorImagePath(String exteriorImagePath) { this.exteriorImagePath = exteriorImagePath;}

    public void setPrimaryKey(String primaryKey) { this.primaryKey = primaryKey; }

    public void setShopkey(String shopKey) {this.shopKey = shopKey;}

}