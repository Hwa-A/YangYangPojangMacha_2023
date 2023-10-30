package com.yuhan.yangpojang.pochaInfo.model;

// pch: pojangmacha
public class ReviewDTO {
    String uid;         // 회원 ID
    String pchName;       // 포차 이름
    double rating;      // 별점
    String content;     // 내용
    String date;        // 작성 날짜
    public String picUrl;     // 사진 url

    // 생성자
    public ReviewDTO() {

    }
    public ReviewDTO(String uid, String pchName) {
        this.uid = uid;
        this.pchName = pchName;
        this.rating = 0;
        this.content = "";
        this.date = "false";
        this.picUrl = "";
    }
    public ReviewDTO(String uid, String pchName, double rating, String content, String date, String picUrl) {
        this.uid = uid;
        this.pchName = pchName;
        this.rating = rating;
        this.content = content;
        this.date = date;
        this.picUrl = picUrl;
    }

    // getter
    public String getUid() {
        return uid;
    }
    public String getPchName() {
        return pchName;
    }
    public double getRating() {
        return rating;
    }
    public String getContent() {
        return content;
    }
    public String getDate() {
        return date;
    }
    public String getPicUrl() { return picUrl; }

    // setter
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setPchName(String pchName) {
        this.pchName = pchName;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setPicUrl(String picUrl) { this.picUrl = picUrl; }
}
