package com.wangzai.nettywebsocket.controller;

import com.wangzai.nettywebsocket.error.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Controller
public class RootController extends BaseController {

    @RequestMapping("/")
    public String login(HttpServletRequest httpServletRequest) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        HttpSession session = httpServletRequest.getSession();
        String user = (String) session.getAttribute("username"); //获取登录的session信息
        if(user!=null){
            return "redirect:/room/list";
        }
        return "index";
    }
}
