package com.yuhan.yangpojang.pochaInfo.model;

// pch: pojangmacha
public class ReviewDTO {
    String uid;         // 회원 ID
    double rating;      // 별점
    String content;     // 내용
    String date;        // 작성 날짜
    public String picUrl1;     // 사진 url 1
    public String picUrl2;     // 사진 url 2
    public String picUrl3;     // 사진 url 3

    // 생성자
    public ReviewDTO() {
    }
}