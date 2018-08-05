package com.kaybo.app.model;

public class User {
    private String userNo;
    private String userKey;
    private String userNm;
    private String userImg;

    public User(String userNo, String userKey, String userNm, String userImg) {
        this.userNo = userNo;
        this.userNm = userNm;
        this.userKey = userKey;
        this.userImg = userImg;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserNm() {
        return userNm;
    }

    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    @Override
    public String toString() {
        return "User{" +
                "userNo='" + userNo + '\'' +
                ", userKey='" + userKey + '\'' +
                ", userNm='" + userNm + '\'' +
                ", userImg='" + userImg + '\'' +
                '}';
    }
}
