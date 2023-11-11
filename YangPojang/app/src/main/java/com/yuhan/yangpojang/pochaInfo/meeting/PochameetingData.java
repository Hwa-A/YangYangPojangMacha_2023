package com.yuhan.yangpojang.pochaInfo.meeting;

public class PochameetingData {

    private String date;
    private String hostUid;
    private String maxAge;
    private String maxMember;
    private String minAge;
    private String registerTime;
    private String time;
    private String title;
    private String yearDate;



    public PochameetingData(){}

    public PochameetingData(String date, String hostUid, String maxAge, String maxMember, String minAge, String registerTime, String time, String title, String yearDate){
        this.date = date;
        this.hostUid = hostUid;
        this.maxAge = maxAge;
        this.maxMember = maxMember;
        this.minAge = minAge;
        this.registerTime = registerTime;
        this.time = time;
        this.title = title;
        this.yearDate = yearDate;
    }

    public String getDate() {
        return date;
    }

    public String getHostUid() {
        return hostUid;
    }

    public String getMaxAge() {
        return maxAge;
    }

    public String getMaxMember() {
        return maxMember;
    }

    public String getMinAge() {
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

    public void setDate(String date) {
        this.date = date;
    }

    public void setHostUid(String hostUid) {
        this.hostUid = hostUid;
    }

    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }

    public void setMaxMember(String maxMember) {
        this.maxMember = maxMember;
    }

    public void setMinAge(String minAge) {
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
}
