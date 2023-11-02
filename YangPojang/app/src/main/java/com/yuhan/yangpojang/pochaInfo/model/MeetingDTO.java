package com.yuhan.yangpojang.pochaInfo.model;
// pch: pojangmacha
public class MeetingDTO {
    private String hostUid;             // 개최 회원 ID
    private String pchKey;          // 포차 고유키
    private String title;           // 번개 소개글
    private String date;            // 번개 날짜(현재)
    private String time;            // 번개 시간
    private int maxMember;          // 번개 정원
    private int minAge;             // 최소 연령대
    private int maxAge;             // 최대 연령대

    // 생성자
    // 데이터 읽는 경우, firebase에서 해당 클래스와 객체 매핑을 위한 생성자
    public MeetingDTO() {
    }

    // getter
    public String getHostUid() {
        return hostUid;
    }
    public String getPchKey() {
        return pchKey;
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
    public int getMaxMember() {
        return maxMember;
    }
    public int getMinAge() {
        return minAge;
    }
    public int getMaxAge() {
        return maxAge;
    }

    // setter
    public void setHostUid(String uid) {
        this.hostUid = hostUid;
    }
    public void setPchKey(String pchKey) {
        this.pchKey = pchKey;
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
    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }
    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }
}