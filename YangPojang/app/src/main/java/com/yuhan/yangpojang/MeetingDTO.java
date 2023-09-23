package com.yuhan.yangpojang;

public class MeetingDTO {
    String uid;     // 회원 ID
    String pchName;     // 포차 이름
    String title;       // 번개 소개글
    String meetDate;        // 번개 날짜
    String date;            // 번개 작성 날짜
    String time;            // 번개 시간
    int maxMember;          // 번개 인원

    // 생성자
    public MeetingDTO(String uid, String pchName) {
        this.uid = uid;
        this.pchName = pchName;
        this.title = "";
        this.meetDate = "";
        this.date = "";
        this.time = "";
        this.maxMember = 0;
    }

    // getter

    public String getUid() {
        return uid;
    }

    public String getPchName() {
        return pchName;
    }

    public String getTitle() {
        return title;
    }

    public String getMeetDate() {
        return meetDate;
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


    // setter
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPchName(String pchName) {
        this.pchName = pchName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMeetDate(String meetDate) {
        this.meetDate = meetDate;
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
}
