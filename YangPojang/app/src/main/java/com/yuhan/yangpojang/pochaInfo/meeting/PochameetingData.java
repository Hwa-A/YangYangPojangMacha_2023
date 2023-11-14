package com.yuhan.yangpojang.pochaInfo.meeting;

public class PochameetingData {

    private String date;
    private String hostUid;
    private int maxAge;
    private int maxMember;
    private int minAge;
    private String registerTime;
    private String time;
    private String title;
    private String yearDate;



    public PochameetingData(){}

    public PochameetingData(String date, String hostUid, int maxAge, int maxMember, int minAge, String registerTime, String time, String title, String yearDate){
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
}
