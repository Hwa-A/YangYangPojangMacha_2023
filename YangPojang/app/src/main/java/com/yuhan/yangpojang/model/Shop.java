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
    private String storeImageUri; //[content://media/external/images/media/~] 형태의 주소
    private String addressName;
    private String category;
    private String hash;
    private String fbStoreImgurl; // 파이어베이스에 들어간 이미지 url [https://firebasestorage.googleapis.com///] 형태

    private boolean verified; //인증 여부
    private boolean hasMeeting; //번개 여부
    private float rating; //가게 별점
    private String geohash;
    private String exteriorImagePath;
    private String primaryKey;
    private String uid;
    private int countVerified;
    private int countSingo;

    private String shopKey;

    //중요" firebase 이용을 위해서는 기본생성자 필수
    public Shop() {
    }

    public Shop(String uid,String shopName,double latitude, double longitude ,String  addressName, boolean pwayMobile, boolean pwayCard,
                boolean pwayAccount, boolean pwayCash, boolean openMon, boolean openTue,
                boolean openWed, boolean openThu, boolean openFri, boolean openSat,
                boolean openSun, String category ,boolean isVerified, boolean hasMeeting, float rating, String geohash ,
                int countVerified, int countSingo,
                String exteriorImagePath) {

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
        this.verified = verified;
        this.hasMeeting =hasMeeting;
        this.rating= rating;
        this.geohash = geohash;
        this.exteriorImagePath=exteriorImagePath;
        this.countVerified=countVerified;
    }

    public String getShopKey() {  return shopKey; }

    public void setShopKey(String shopKey) { this.shopKey = shopKey; }
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
    public String getExteriorImagePath() { return exteriorImagePath;   }


    public String getPrimaryKey() { return primaryKey; }

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


    public int getCountVerified() {
        return countVerified;
    }

    public void setCountVerified(int countVerified) {
        this.countVerified = countVerified;
    }

    public int getCountSingo() {  return countSingo; }

    public void setCountSingo(int countSingo) {  this.countSingo = countSingo; }

    @Override
    public String toString() {
        return "Shop{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", shopName='" + shopName + '\'' +
                ", pwayMobile=" + pwayMobile +
                ", pwayCard=" + pwayCard +
                ", pwayAccount=" + pwayAccount +
                ", pwayCash=" + pwayCash +
                ", openMon=" + openMon +
                ", openTue=" + openTue +
                ", openWed=" + openWed +
                ", openThu=" + openThu +
                ", openFri=" + openFri +
                ", openSat=" + openSat +
                ", openSun=" + openSun +
                ", storeImageUri='" + storeImageUri + '\'' +
                ", addressName='" + addressName + '\'' +
                ", category='" + category + '\'' +
                ", hash='" + hash + '\'' +
                ", fbStoreImgurl='" + fbStoreImgurl + '\'' +
                ", verified=" + verified +
                ", hasMeeting=" + hasMeeting +
                ", rating=" + rating +
                ", geohash='" + geohash + '\'' +
                ", exteriorImagePath='" + exteriorImagePath + '\'' +
                ", primaryKey='" + primaryKey + '\'' +
                ", uid='" + uid + '\'' +
                ", shopKey='" + shopKey + '\'' +
                '}';
    }
}