package com.kaybo.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameInfo {
    private String gameNo;
    private String gameNm;
    private String gameUrl;
    private String gameCash;

    public String getGameNo() {
        return gameNo;
    }

    public void setGameNo(String gameNo) {
        this.gameNo = gameNo;
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

    public String getGameCash() {
        return gameCash;
    }

    public void setGameCash(String gameCash) {
        this.gameCash = gameCash;
    }
}
