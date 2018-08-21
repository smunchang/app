package com.kaybo.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaybo.app.AppException;
import com.kaybo.app.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Aspect
@Component
public class AspectService {

    private static Log logger = LogFactory.getLog(AspectService.class);

    @Value("${auth.url}")
    private String authUrl;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Before("execution(* com.kaybo.app.controller.GameController.*(..))")
    public void onBeforeHandler(JoinPoint joinPoint){

        ServletRequestAttributes t = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = t.getRequest();
        String userNo = request.getHeader("userNo") ;
        String userKey = request.getHeader("userKey") ;


        if(request.getServletPath().contains("/user")){

        }else{
            User user = sqlSessionTemplate.selectOne("user.selectUser", userNo);
            if(!userKey.equals(user.getUserKey())){
                throw new AppException(9999, "Authentication Error");
            }
        }

        /*    try{
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.add("memberid", userNo);
                headers.add("authorization", userKey);
                headers.setContentType(MediaType.APPLICATION_JSON);
                ResponseEntity<String> response = null;
                try{
                    response = restTemplate.exchange(authUrl, HttpMethod.GET, new HttpEntity(headers), String.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }

                if(response.getStatusCode() == HttpStatus.OK){
                    Map<String,String> map = new HashMap<String,String>();
                    ObjectMapper mapper = new ObjectMapper();
                    map = mapper.readValue(response.getBody(), new TypeReference<HashMap<String,String>>(){});

                    logger.info(response.getBody());
//User(String userNo, String userId, String userKey, String userNm, String userImg)
                    //User u = new User(userNo, map.get("UserID"), userKey, map.get("UserName"), map.get("profileImage"));
                    //User u = new User(map.get("UserUID"), map.get("UserID"), userKey, map.get("UserName"), map.get("profileImage"));
                    User u = new User(map.get("memberId"), map.get("nickName"), userKey, map.get("userName"), map.get("profileImage"));

                    sqlSessionTemplate.insert("user.insertUser", u);
                }else{
                    throw new AppException(9999, "Authentication Error");
                }
            }catch (Exception e){
                throw new AppException(9999, "Authentication Error");
            }



        }else{
            if(!userKey.equals(user.getUserKey())){
                throw new AppException(9999, "Authentication Error");
            }
        }*/

    }


}
