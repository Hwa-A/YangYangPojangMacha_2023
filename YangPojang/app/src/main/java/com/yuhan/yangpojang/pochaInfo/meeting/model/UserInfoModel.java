package com.yuhan.yangpojang.pochaInfo.meeting.model;

public class UserInfoModel {

    private String UID;
    private String nick;
    private String brith;
    private int age;

    public UserInfoModel(){      }

    public void setUserInfoModel(String UID, String nick, String brith, int age) {
        this.UID = UID;
        this.nick = nick;
        this.brith = brith;
        this.age = age;
    }


    public String getUID() {        return UID;    }

    public String getNick() {        return nick;    }

    public String getBrith() {        return brith;    }

    public int getAge() {        return age;    }

    public void setUID(String UID) {        this.UID = UID;    }

    public void setNick(String nick) {        this.nick = nick;    }

    public void setBrith(String brith) {        this.brith = brith;    }

    public void setAge(int age) {        this.age = age;    }

    @Override
    public String toString() {
        return "UserInfoModel{" +
                "UID='" + UID + '\'' +
                ", nick='" + nick + '\'' +
                ", brith='" + brith + '\'' +
                ", age=" + age +
                '}';
    }
}
