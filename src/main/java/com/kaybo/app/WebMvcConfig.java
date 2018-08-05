package com.kaybo.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AppInterceptor()).addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        registry.addMapping("/**").allowedOrigins("*")
                .allowedHeaders("Content-Type","userNo", "userKey")
                .exposedHeaders("Content-Type","userNo", "userKey")
                .allowCredentials(false).maxAge(3600);

                //.allowedOrigins("http://localhost:8080")
                //.allowedHeaders("userNo", "userKey");
    }


}
