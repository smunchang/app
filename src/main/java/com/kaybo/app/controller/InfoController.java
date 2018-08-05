package com.kaybo.app.controller;

import com.kaybo.app.model.GameInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
public class InfoController {

    private static Log logger = LogFactory.getLog(InfoController.class);

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;


    @GetMapping("/info/game/{gameNo}")
    @ResponseBody
    public GameInfo selectGame(@PathVariable String gameNo) throws IOException {

        GameInfo game = sqlSessionTemplate.selectOne("game.selectGame", gameNo);
        return game;
    }

    @GetMapping("/info/allgame")
    @ResponseBody
    public List<GameInfo> listGame() throws IOException {

        List<GameInfo> gameList = sqlSessionTemplate.selectList("game.listGame");
        return gameList;
    }


    @PostMapping("/info/insertgame")
    @ResponseBody
    public void insertGame(@RequestBody GameInfo game) {

        sqlSessionTemplate.insert("game.insertGame", game);

    }

    @PostMapping("/info/deletegame/{gameNo}")
    @ResponseBody
    public void deleteGame(@PathVariable String gameNo) {

        sqlSessionTemplate.delete("game.deleteGame", gameNo);

    }

}
