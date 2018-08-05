package com.kaybo.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AppInterceptor extends HandlerInterceptorAdapter {
    private static Log logger = LogFactory.getLog(AppInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String reqUrl = request.getRequestURL().toString();

        if(reqUrl.contains("/info/")) return true;

        String userNo = request.getHeader("userNo") ;
        String userKey = request.getHeader("userKey") ;

        if(userNo == null || userKey == null){
            throw new AppException( 1111, "Bad Header");
        }



//        if(userNo.equals("a")){
//            throw new AppException(999, "Oh dear!");
//        }
        return true;
    }

    @Override
    public void postHandle( HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {}
}
