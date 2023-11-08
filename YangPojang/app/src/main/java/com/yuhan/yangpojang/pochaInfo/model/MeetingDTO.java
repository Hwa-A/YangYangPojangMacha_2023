package com.yuhan.yangpojang.pochaInfo.model;

import android.text.TextUtils;

import java.io.Serializable;

// pch: pojangmacha
public class MeetingDTO implements Serializable {
    private String hostUid;             // 개최 회원 ID
    private String title;           // 번개 소개글
    private String yearDate;        // 번개 날짜(당일): 년도 O
    private  String date;           // 번개 날짜(당일): 년도 X
    private String time;            // 번개 시간
    private int maxMember = 0;          // 번개 정원
    private int minAge = 0;             // 최소 연령대
    private int maxAge = 0;             // 최대 연령대
    private String registerTime;    // 번개 등록 시간

    // 생성자
    // 데이터 읽는 경우, firebase에서 해당 클래스와 객체 매핑을 위한 생성자
    public MeetingDTO() {
    }

    // getter
    public String getHostUid() {
        return hostUid;
    }

    public String getTitle() {
        return title;
    }

    public String getYearDate() {
        return yearDate;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getMaxMember() {
        return maxMember;
    }

    public int getMinAge() {
        return minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    // setter
    public void setHostUid(String hostUid) {
        this.hostUid = hostUid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYearDate(String yearDate) {
        this.yearDate = yearDate;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }
}