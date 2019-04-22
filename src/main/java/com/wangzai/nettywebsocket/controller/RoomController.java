package com.wangzai.nettywebsocket.controller;

import com.wangzai.nettywebsocket.error.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


@RequestMapping("/room")
@Controller
public class RoomController extends BaseController {

    @RequestMapping(value = "/list")
    @ResponseBody
    public String login() throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        return "home/roomlist";
    }
}
