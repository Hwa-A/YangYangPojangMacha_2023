package com.yuhan.yangpojang.mypage.Model.MeetingModelCollection;

import com.yuhan.yangpojang.model.Shop;

import java.util.ArrayList;

public class AllMeetingItemModel {
    String meetingId;
    String shopId;

    Shop shop;
    String title;
    String time;
    int maxAge;
    int minAge;
    int maxMember;
    int countAttenders;
    String addressName;

    ArrayList<MeetingAttendersModel> attenders;

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
    public Shop getShop(){return shop;}
    public void setShop(Shop shop){this.shop = shop;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }
    public int getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }
    public int getCountAttenders() {
        return countAttenders;
    }

    public void setCountAttenders(int countAttenders) {
        this.countAttenders = countAttenders;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public ArrayList<MeetingAttendersModel> getAttenders() {
        return attenders;
    }

    public void setAttenders(ArrayList<MeetingAttendersModel> attenders) {
        this.attenders = attenders;
    }

    @Override
    public String toString() {
        return "AllMeetingItemModel{" +
                "meetingId='" + meetingId + '\'' +
                ", shopId='" + shopId + '\'' +
                ", shop='" + shop + '\'' +
                ", title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", maxAge=" + maxAge +
                ", minAge=" + minAge +
                ", maxMember=" + maxMember +
                ", countAttenders=" + countAttenders +
                ", addressName='" + addressName + '\'' +
                ", attenders='" + attenders + '\'' +
                '}';
    }
}
