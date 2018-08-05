package com.kaybo.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameUser {
    private String gameNo;
    private String userNo;
    private String userNm;
    private String userImg;
    private long score;
    private int rank;
    private String gameData;


    @JsonIgnore
    private long lastUpdate;
    private long cash;

    private String gameNm;
    private String gameUrl;

    public String getGameNo() {
        return gameNo;
    }

    public void setGameNo(String gameNo) {
        this.gameNo = gameNo;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
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

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getGameData() {
        return gameData;
    }

    public void setGameData(String gameData) {
        this.gameData = gameData;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getCash() {
        return cash;
    }

    public void setCash(long cash) {
        this.cash = cash;
    }

    public String getGameNm() {
        return gameNm;
    }

    public void setGameNm(String gameNm) {
        this.gameNm = gameNm;
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }
}
