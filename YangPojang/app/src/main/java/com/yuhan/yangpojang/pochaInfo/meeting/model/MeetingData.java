package com.yuhan.yangpojang.pochaInfo.meeting.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MeetingData {

    private String date;
    private String hostUid;
    private int maxAge;
    private int maxMember;
    private int nowMember;
    private int minAge;
    private String registerTime;
    private String time;
    private String title;
    private String yearDate;
    private String meetingKey;
    private String pochaKey;

    private Map<String, String> attends;



    public MeetingData(){}

    public MeetingData(String date, String hostUid, int maxAge, int maxMember, int minAge,
                       String registerTime, String time, String title, String yearDate,
                       String meetingKey, String pochaKey){

        this.date = date;
        this.hostUid = hostUid;
        this.maxAge = maxAge;
        this.maxMember = maxMember;
        this.minAge = minAge;
        this.registerTime = registerTime;
        this.time = time;
        this.title = title;
        this.yearDate = yearDate;
        this.meetingKey = meetingKey;
        this.pochaKey = pochaKey;

    }

    public MeetingData(String date, String hostUid, int maxAge, int maxMember, int minAge,
                            String registerTime, String time, String title, String yearDate,
                       String meetingKey,Map<String,String> attends, int nowMember){

        this.date = date;
        this.hostUid = hostUid;
        this.maxAge = maxAge;
        this.maxMember = maxMember;
        this.minAge = minAge;
        this.registerTime = registerTime;
        this.time = time;
        this.title = title;
        this.yearDate = yearDate;
        this.meetingKey = meetingKey;
        this.attends = attends;
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

    public String getMeetingKey() {        return meetingKey;    }

    public Map<String,String> getAttends() {
        return attends;
    }

    public int getNowMember() {    return nowMember;    }

    public String getPochaKey() {        return pochaKey;    }

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

    public void setMeetingKey(String meetingKey) {     this.meetingKey = meetingKey;    }

    public void setAttends(Map<String,String> attends) {
        this.attends = attends;
    }

    public void setNowMember(int nowMember) {    this.nowMember = nowMember;    }

    public void setPochaKey(String pochaKey) {        this.pochaKey = pochaKey;    }
}
