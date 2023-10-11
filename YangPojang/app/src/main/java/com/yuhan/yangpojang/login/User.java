package com.yuhan.yangpojang.login;

public class User {

    private String User_Nickname;
    private String User_birthday;
    private String User_sex;
    private String User_uid;

    public User(){}


    public String getUser_uid() {
        return User_uid;
    }

    public void setUser_uid(String user_uid) {
        this.User_uid = user_uid;
    }

    public String getUser_Nickname() {
        return User_Nickname;
    }

    public void setUser_Nickname(String user_Nickname) {
        this.User_Nickname = user_Nickname;
    }

    public String getUser_birthday() {
        return User_birthday;
    }

    public void setUser_birthday(String user_birthday) {
        this.User_birthday = user_birthday;
    }

    public String getUser_sex() {
        return User_sex;
    }

    public void setUser_sex(String user_sex) {
        this.User_sex = user_sex;
    }

    public User(String user_Nickname, String user_birthday, String user_sex, String user_uid) {
        this.User_Nickname = user_Nickname;
        this.User_birthday = user_birthday;
        this.User_sex = user_sex;
        this.User_uid = user_uid;
    }



}

