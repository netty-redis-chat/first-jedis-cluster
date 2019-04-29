package com.wangzai.nettywebsocket.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             //Object 是被拦截的对象（Controller实例？）
                             Object o) throws Exception {
        //拦截器不能过滤静态资源
        
        //编码过滤器
        //httpServletRequest.setCharacterEncoding("uft-8");
        
        //登录验证                           未登录自动跳转界面 forward显示不跳转？
        httpServletRequest.getRequestDispatcher("/").forward(httpServletRequest, httpServletResponse);
        
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/");  //未登录自动跳转界面
            return false;
        }
        String user = (String) session.getAttribute("username"); //获取登录的session信息
        if(user!=null){
            return true;
        }
        else{
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/");  //未登录自动跳转界面
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //可修改传入视图（是View不是ModelAndView!）的参数和视图的名称
        //modelAndView.addObject("list", new ArrayList<String>());
        //modelAndView.setViewName("/home/roomlist");
    }

    //onDestroyed方法，请求响应后调用，关闭资源，不常用
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
