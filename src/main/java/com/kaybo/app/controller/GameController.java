package com.kaybo.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaybo.app.AppException;
import com.kaybo.app.model.GameInfo;
import com.kaybo.app.model.GameUser;
import com.kaybo.app.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:8000")
@RestController
public class GameController {

    private static Log logger = LogFactory.getLog(GameController.class);

    @Value("${cash.url}")
    private String cashUrl;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;


    @GetMapping("/game/{gameNo}/user")
    @ResponseBody
    public GameUser getGameUser(@RequestHeader(value="userNo") String userNo,
                                @PathVariable String gameNo) throws IOException {

        GameUser game = new GameUser();
        game.setGameNo(gameNo);
        game.setUserNo(userNo);
        GameUser gameResult = sqlSessionTemplate.selectOne("game.selectGameUser", game);

        User user = sqlSessionTemplate.selectOne("user.selectUser", userNo);


        if(gameResult != null){
            if(gameResult.getGameData() != null){
               /* Map<String,String> map = new HashMap<String,String>();
                ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(gameResult.getGameDataString(), new TypeReference<HashMap<String,String>>(){});
                gameResult.setGameData(map);*/
                gameResult.setUserNm(user.getUserNm());
                gameResult.setUserImg(user.getUserImg());
            }

            return gameResult;
        }else{
            gameResult = new GameUser();
            gameResult.setScore(0);
            gameResult.setUserNm(user.getUserNm());
            gameResult.setUserImg(user.getUserImg());
            return gameResult;
        }
    }

    @PostMapping("/game/{gameNo}/score")
    @ResponseBody
    public void setGameScore(@RequestHeader(value="userNo") String userNo,
                             @PathVariable String gameNo, @RequestBody GameUser game) {

        game.setGameNo(gameNo);
        game.setUserNo(userNo);

        GameUser result = sqlSessionTemplate.selectOne("game.selectGameUser", game);
        if(result == null){
            sqlSessionTemplate.insert("game.insertGameUser", game);
        }else{
            if(game.getScore() > result.getScore()){
                sqlSessionTemplate.update("game.updateGameScore", game);
            }
        }

    }


    @GetMapping("/game/{gameNo}/ranking/{limit}")
    @ResponseBody
    public List<GameUser> getGameRanking(@PathVariable String gameNo, @PathVariable int limit) {

        RowBounds rowBounds = new RowBounds(0, limit);

        List<GameUser> result = sqlSessionTemplate.selectList("game.listGameRank", gameNo, rowBounds);

        return result;
    }



    @PostMapping("/game/{gameNo}/data")
    @ResponseBody
    public void setGameData(@RequestHeader(value="userNo") String userNo,
                            @PathVariable String gameNo, @RequestBody String gameData) throws Exception{

        ObjectMapper mapper = new ObjectMapper();


        GameUser game = new GameUser();
        game.setGameNo(gameNo);
        game.setUserNo(userNo);
        //game.setGameDataString(mapper.writeValueAsString(gameData));
        game.setGameData(gameData);

        GameUser result = sqlSessionTemplate.selectOne("game.selectGameUser", game);
        if(result == null){
            sqlSessionTemplate.insert("game.insertGameUser", game);
        }else{
            sqlSessionTemplate.update("game.updateGameData", game);
        }

    }


    @PostMapping("/game/{gameNo}/cash")
    @ResponseBody
    public void setGameCash(@RequestHeader(value="userNo") String userNo,
                            @PathVariable String gameNo, @RequestBody GameUser game,
                            HttpServletRequest httpServletRequest){

        //TODO
        //kpcash 연동
        GameInfo gameInfo = sqlSessionTemplate.selectOne("game.selectGame", gameNo);
        User user = sqlSessionTemplate.selectOne("user.selectUser", userNo);
        if(gameInfo.getGameCash().equals("SF1")){
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("userno",userNo);
            map.add("userid",user.getUserNm());
            map.add("gamecode", "");
            map.add("itemid", "");
            map.add("itemname", "");
            map.add("chargeamt", game.getCash() + "");
            map.add("ipaddr", httpServletRequest.getRemoteAddr());

            HttpEntity<?> httpEntity = new HttpEntity<Object>(map, headers);

            try{
                ResponseEntity<Object> response = restTemplate.exchange(cashUrl, HttpMethod.POST, httpEntity, Object.class);
            }catch (Exception ex){
                ex.printStackTrace();
            }


            //logger.info(response.getBody());

        }else{

        }


        boolean kpCashResult = true;
        //game.getCash();

        if(kpCashResult){
            ObjectMapper mapper = new ObjectMapper();
            //GameUser game = new GameUser();
            game.setGameNo(gameNo);
            game.setUserNo(userNo);
            //game.setGameData(mapper.writeValueAsString(game.getGameData()));

            GameUser result = sqlSessionTemplate.selectOne("game.selectGameUser", game);
            if(result == null){
                sqlSessionTemplate.insert("game.insertGameUser", game);
            }else{
                sqlSessionTemplate.update("game.updateGameData", game);
            }
        }else{
            throw new AppException( -10000, "fail to use kpcash");
        }



    }


}
