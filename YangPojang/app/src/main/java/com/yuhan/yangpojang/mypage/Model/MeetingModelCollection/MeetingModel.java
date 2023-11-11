package com.yuhan.yangpojang.mypage.Model.MeetingModelCollection;

public class MeetingModel {
    String date; // 날짜
    String hostUid; //주최자 uid
    int maxAge; //최대나이
    int maxMember; //최대인원
    int minAge; //최소나이
    String registerTime; //등록시간
    String time; // 시간
    String title; // 번개 글
    String yearDate; // 날짜

    public void setDate(String date) {
        this.date = date;
    }

    public void setHostUid(String hostUid) {
        this.hostUid = hostUid;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYearDate(String yearDate) {
        this.yearDate = yearDate;
    }

    public String getDate() {
        return date;
    }

    public String getHostUid() {
        return hostUid;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getMaxMember() {
        return maxMember;
    }

    public int getMinAge() {
        return minAge;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getYearDate() {
        return yearDate;
    }

    @Override
    public String toString() {
        return "MyMeetingModel{" +
                "date='" + date + '\'' +
                ", hostUid='" + hostUid + '\'' +
                ", maxAge=" + maxAge +
                ", maxMember=" + maxMember +
                ", minAge=" + minAge +
                ", registerTime='" + registerTime + '\'' +
                ", time='" + time + '\'' +
                ", title='" + title + '\'' +
                ", yearDate='" + yearDate + '\'' +
                '}';
    }
}
