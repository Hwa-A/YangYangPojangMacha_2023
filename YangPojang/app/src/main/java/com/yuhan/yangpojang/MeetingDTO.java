package com.yuhan.yangpojang;
// pch: pojangmacha
public class MeetingDTO {
    String uid;             // 회원 ID
    String pchName;         // 포차 이름
    String title;           // 번개 소개글
    String date;            // 번개 날짜
    String time;            // 번개 시간
    String writeDate;       // 번개 작성 날짜
    double maxMember;       // 번개 정원

    // 생성자
    public MeetingDTO(String uid, String pchName) {
        this.uid = uid;
        this.pchName = pchName;
        this.title = "";
        this.date = "";
        this.time = "";
        this.writeDate = "";
        this.maxMember = 0;
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
    public void setDate(String date) {
        this.date = date;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }
    public void setMaxMember(double maxMember) {
        this.maxMember = maxMember;
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
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getWriteDate() {
        return writeDate;
    }
    public double getMaxMember() {
        return maxMember;
    }
}
