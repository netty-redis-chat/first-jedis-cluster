package com.wangzai.nettywebsocket.config;

import com.wangzai.nettywebsocket.interceptor.RoomInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
        //登录拦截器
//        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").excludePathPatterns("/user/login").excludePathPatterns("/");
        registry.addInterceptor(new RoomInterceptor()).addPathPatterns("/**").excludePathPatterns("/room/exit");

    }
}
