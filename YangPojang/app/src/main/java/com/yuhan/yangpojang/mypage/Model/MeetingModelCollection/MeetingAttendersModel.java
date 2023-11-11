package com.yuhan.yangpojang.mypage.Model.MeetingModelCollection;

public class MeetingAttendersModel {
    String attender;
    String nickName;

    public String getAttender() {
        return attender;
    }

    public void setAttender(String attender) {
        this.attender = attender;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "MeetingAttendersModel{" +
                "attender='" + attender + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
