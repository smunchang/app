package com.kaybo.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaybo.app.AppException;
import com.kaybo.app.model.*;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:8000")
@RestController
public class GameController {

    private static Log logger = LogFactory.getLog(GameController.class);

    @Value("${cash.sf1.url}")
    private String cashSf1Url;
    @Value("${item.url}")
    private String itemUrl;
    @Value("${auth.url}")
    private String authUrl;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;


    @GetMapping("/game/{gameNo}/user")
    @ResponseBody
    public GameUser getGameUser(@RequestHeader(value="userNo") String userNo, @RequestHeader(value="userKey") String userKey,
                                @PathVariable String gameNo) throws IOException {
//////////////
        User cu = sqlSessionTemplate.selectOne("user.selectUser", userNo);
        if(cu == null) {
            User u = new User(userNo, "temp", "temp", "temp", "temp");
            sqlSessionTemplate.insert("user.insertUser", u);
        }

        try{
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("memberid", userNo);
            headers.add("authorization", userKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<String> response = null;
            try{
                response = restTemplate.exchange(authUrl, HttpMethod.POST, new HttpEntity(headers), String.class);
                logger.info((response.getBody()));

            }catch (Exception ex){
                ex.printStackTrace();
            }

            if(response.getStatusCode() == HttpStatus.OK){
                Map<String,String> map = new HashMap<String,String>();
                ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(response.getBody(), new TypeReference<HashMap<String,String>>(){});

                logger.info(response.getBody());
//User(String userNo, String userId, String userKey, String userNm, String userImg)
                User u = new User(map.get("memberId"), map.get("nickname"), userKey, map.get("userName"), map.get("profileImage"));

                sqlSessionTemplate.update("user.updateUser", u);
            }else{
                throw new AppException(9999, "Authentication Error");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new AppException(9999, "Authentication Error");
        }

        GameUser game = new GameUser();
        game.setGameNo(gameNo);
        game.setUserNo(userNo);
        GameUser gameResult = sqlSessionTemplate.selectOne("game.selectGameUserDetail", game);

        long cash = 0;
        User uuu = sqlSessionTemplate.selectOne("user.selectUser", userNo);

        logger.info(uuu.getUserNm());
        GameInfo gameInfo = sqlSessionTemplate.selectOne("game.selectGame", gameNo);
        //User user = sqlSessionTemplate.selectOne("user.selectUser", userNo);
        if(gameInfo.getGameCash().equals("SF1")){
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("userno",userNo);
            map.add("userid",uuu.getUserId());

            HttpEntity<?> httpEntity = new HttpEntity<Object>(map, headers);

            try{
                ResponseEntity<String> response = restTemplate.exchange(cashSf1Url + "/GetSF1Balance", HttpMethod.POST, httpEntity, String.class);
                //{retcode=0, gcashreal=0.00, gcashbonus=9800.00, retmsg=ok}
                logger.info(response.getBody());
                if(response.getStatusCode() == HttpStatus.OK){
                    Map<String,String> map1 = new HashMap<String,String>();
                    ObjectMapper mapper = new ObjectMapper();
                    map1 = mapper.readValue(response.getBody(), new TypeReference<HashMap<String,String>>(){});

                    logger.info(response.getBody());
                    if(map1.get("retcode").equals("0")){
                        double gcashreal = Double.parseDouble(map1.get("gcashreal"));
                        double gcashbonus = Double.parseDouble(map1.get("gcashbonus"));
                        cash = (long) (gcashreal + gcashbonus);
                    }else{
                        throw new AppException(-4444, "fail to get balance");
                    }


                }else{
                    throw new AppException(-5678, "Bad Request");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }




        }else{

        }

        if(gameResult != null){
            if(gameResult.getGameData() != null){
               /* Map<String,String> map = new HashMap<String,String>();
                ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(gameResult.getGameDataString(), new TypeReference<HashMap<String,String>>(){});
                gameResult.setGameData(map);*/
                gameResult.setUserId(uuu.getUserId());
                gameResult.setUserNm(uuu.getUserNm());
                gameResult.setUserImg(uuu.getUserImg());
            }


        }else{
            gameResult = new GameUser();
            gameResult.setScore(0);
            gameResult.setUserId(uuu.getUserId());
            gameResult.setUserNm(uuu.getUserNm());
            gameResult.setUserImg(uuu.getUserImg());
        }
        gameResult.setCash(cash);
        return gameResult;
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

        boolean guExist = sqlSessionTemplate.selectOne("game.existGameUser", game);
        if(!guExist){
            sqlSessionTemplate.insert("game.insertGameUser", game);
        }else{
            sqlSessionTemplate.update("game.updateGameData", game);
        }

    }


    @PostMapping("/game/{gameNo}/useCash")
    @ResponseBody
    public void useCash(@RequestHeader(value="userNo") String userNo,
                            @PathVariable String gameNo, @RequestBody GameItem gameItem,
                            HttpServletRequest httpServletRequest){

        boolean cashResult = true;
        GameInfo gameInfo = sqlSessionTemplate.selectOne("game.selectGame", gameNo);
        User user = sqlSessionTemplate.selectOne("user.selectUser", userNo);
        if(gameInfo.getGameCash().equals("SF1")){
            try{
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
                map.add("userno",userNo);
                map.add("userid",user.getUserId());
                map.add("gamecode", gameInfo.getGameCash());
                map.add("itemid", gameItem.getItemId());
                map.add("itemname", gameItem.getItemName());
                map.add("chargeamt", gameItem.getCash() + "");
                map.add("ipaddr", gameItem.getIpAddr());

                logger.info(map.toString());
                HttpEntity<?> httpEntity = new HttpEntity<Object>(map, headers);


                ResponseEntity<Object> response = restTemplate.exchange(cashSf1Url + "/PurchaseSF1Item", HttpMethod.POST, httpEntity, Object.class);
                if(response.getStatusCode() == HttpStatus.OK){
                    cashResult = true;
                }else{
                    cashResult = false;
                }
                logger.info(response.getBody());
            }catch (Exception ex){
                cashResult = false;
                ex.printStackTrace();
            }


        }else{

        }


        if(cashResult){
            ObjectMapper mapper = new ObjectMapper();
            GameUser game = new GameUser();
            game.setGameNo(gameNo);
            game.setUserNo(userNo);
            game.setGameData(gameItem.getGameData());

            GameUser result = sqlSessionTemplate.selectOne("game.selectGameUser", game);
            if(result == null){
                sqlSessionTemplate.insert("game.insertGameUser", game);
            }else{
                sqlSessionTemplate.update("game.updateGameData", game);
            }
        }else{
            throw new AppException( -10000, "fail to use cash");
        }



    }


    @PostMapping("/game/{gameNo}/getCash")
    @ResponseBody
    public void getCash(@RequestHeader(value="userNo") String userNo,
                            @PathVariable String gameNo, @RequestBody GameItem gameItem,
                            HttpServletRequest httpServletRequest){

        boolean cashResult = true;
        GameInfo gameInfo = sqlSessionTemplate.selectOne("game.selectGame", gameNo);
        User user = sqlSessionTemplate.selectOne("user.selectUser", userNo);
        if(gameInfo.getGameCash().equals("SF1")){
            try{
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


                MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
                map.add("userno",userNo);
                map.add("userid",user.getUserId());
                map.add("username",user.getUserNm());
                map.add("gamecode", gameInfo.getGameCash());
                Date date = new Date(); //2018 0731 001
//todo "retcode=9070, retmsg=Payloadkey is wrong"
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                map.add("payloadkey", "key" + System.nanoTime() +"");
                map.add("flags", "5");
                map.add("cashtype", "2");
                map.add("gcashamt", gameItem.getCash() + "");
                map.add("paytoolname", "issue GameMoney");
                map.add("ipaddr", gameItem.getIpAddr());

                logger.info(map.toString());
                HttpEntity<?> httpEntity = new HttpEntity<Object>(map, headers);


                ResponseEntity<Object> response = restTemplate.exchange(cashSf1Url + "/InsSF1Cash", HttpMethod.POST, httpEntity, Object.class);
                logger.info(response.getBody());
                if(response.getStatusCode() != HttpStatus.OK){
                    throw new AppException( -20000, "fail to get cash");
                }

            }catch (Exception ex){
                ex.printStackTrace();
                throw new AppException( -20001, "fail to get cash");
            }

        }

    }



    @GetMapping("/game/{gameNo}/gamedata")
    @ResponseBody
    public String getGameData(@RequestHeader(value="userNo") String userNo,
                                @PathVariable String gameNo) throws IOException {

        GameUser game = new GameUser();
        game.setGameNo(gameNo);
        game.setUserNo(userNo);
        GameUser gameUser = sqlSessionTemplate.selectOne("game.selectGameUser", game);

        if(gameUser == null){
            sqlSessionTemplate.insert("game.insertGameUser", game);
            gameUser = sqlSessionTemplate.selectOne("game.selectGameUser", game);
        }
        return gameUser.getGameData();
    }


    @PostMapping("/game/{gameNo}/getItem")
    @ResponseBody
    public String getItem(@RequestHeader(value="userNo") String userNo,
                      @PathVariable String gameNo,
                      @RequestBody ItemRequest itemRequest) throws IOException {

        RestTemplate restTemplate1 = new RestTemplate();
        /*ItemRequest itemRequest  = new ItemRequest();
        itemRequest.setCpCode("SLOT");
        itemRequest.setGameCode("SF");
        itemRequest.setUserUid(Integer.parseInt(userNo));
        itemRequest.setItemId(history.getType());*/

        ResponseEntity<String> response = restTemplate1.postForEntity(itemUrl, itemRequest, String.class);


        logger.info(response.getBody());
        if(response.getStatusCode() != HttpStatus.OK){
            throw new AppException(-9003, "Bad Request");
        }
//getBody :{"message":{"result":404,"result_msg":"user not found"},"status":200}
//getBody :{"message":{"result":200,"result_msg":"success"},"status":200}

        return response.getBody();
/*        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(response.getBody());

        JsonNode message = actualObj.get("message");

        String result = message.get("result").toString();
        String result_message = message.get("result_msg").toString();

        if(!result.equals("200")){
            throw new AppException(Integer.parseInt(result), result_message);
        }*/

   }



}
