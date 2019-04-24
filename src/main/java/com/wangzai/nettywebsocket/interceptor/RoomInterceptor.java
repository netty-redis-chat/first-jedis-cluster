package com.wangzai.nettywebsocket.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RoomInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null) {
            return true;
        }
        Integer room = (Integer) session.getAttribute("room"); //获取登录的session信息
        if(room==null){
            return true;
        }else if(httpServletRequest.getRequestURI().equals("/room/"+room)){
            return true;
        }
        else{
            System.out.println(httpServletRequest.getRequestURI());
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/room/"+room);  //未登录自动跳转界面
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
