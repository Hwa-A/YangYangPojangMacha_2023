package com.yuhan.yangpojang.login;

public class User {

    public String user_Nickname;
    public String user_birthday;
    public String user_sex;


    public String getUser_Nickname() {
        return user_Nickname;
    }

    public void setUser_Nickname(String user_Nickname) {
        this.user_Nickname = user_Nickname;
    }

    public String getUser_birthday() {
        return user_birthday;
    }

    public void setUser_birthday(String user_birthday) {
        this.user_birthday = user_birthday;
    }

    public String getUser_sex() {
        return user_sex;
    }

    public void setUser_sex(String user_sex) {
        this.user_sex = user_sex;
    }

    public User(String user_Nickname, String user_birthday, String user_sex) {
        this.user_Nickname = user_Nickname;
        this.user_birthday = user_birthday;
        this.user_sex = user_sex;
    }



}

