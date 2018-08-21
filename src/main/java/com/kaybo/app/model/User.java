package com.kaybo.app.model;

public class User {
    private String userNo = null;
    private String userId = null;

    private String userNm = null;
    private String userImg = null;
    private String userKey = null;

    public User(String userNo, String userId, String userKey, String userNm, String userImg) {
        this.userNo = userNo;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
