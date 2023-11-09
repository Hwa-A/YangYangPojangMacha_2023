package com.yuhan.yangpojang.mypage.Model.MeetingModelCollection;


public class MyMeetingModel {
    String meetingId;
    String shopId;

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public String getShopId() {
        return shopId;
    }

    @Override
    public String toString() {
        return "MyMeetingModel{" +
                "meetingId='" + meetingId + '\'' +
                ", shopId='" + shopId + '\'' +
                '}';
    }
}
