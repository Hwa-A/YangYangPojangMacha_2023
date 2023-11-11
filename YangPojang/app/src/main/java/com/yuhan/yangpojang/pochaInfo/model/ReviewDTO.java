package com.yuhan.yangpojang.pochaInfo.model;

import java.io.Serializable;

// pch: pojangmacha
public class ReviewDTO implements Serializable {
    String uid;         // 작성 회원 id
    float rating = 0;      // 별점
    String summary;     // 내용
    String yearDate;        // 등록 날짜
    private String registerTime;    // 리뷰 등록 시간
    public String picUrl1;     // 사진 url 1
    public String picUrl2;     // 사진 url 2
    public String picUrl3;     // 사진 url 3

    // 생성자
    // 데이터 읽는 경우, firebase에서 해당 클래스와 객체 매핑을 위한 생성자
    public ReviewDTO() {
    }

    // getter()
    public String getUid() {
        return uid;
    }

    public float getRating() {
        return rating;
    }

    public String getSummary() {
        return summary;
    }

    public String getYearDate() {
        return yearDate;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public String getPicUrl1() {
        return picUrl1;
    }

    public String getPicUrl2() {
        return picUrl2;
    }

    public String getPicUrl3() {
        return picUrl3;
    }

    // setter()

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setYearDate(String yearDate) {
        this.yearDate = yearDate;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public void setPicUrl1(String picUrl1) {
        this.picUrl1 = picUrl1;
    }

    public void setPicUrl2(String picUrl2) {
        this.picUrl2 = picUrl2;
    }

    public void setPicUrl3(String picUrl3) {
        this.picUrl3 = picUrl3;
    }
}