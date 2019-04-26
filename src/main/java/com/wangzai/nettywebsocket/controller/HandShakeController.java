package com.wangzai.nettywebsocket.controller;

import com.wangzai.nettywebsocket.response.CommonReturnType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HandShakeController {

    @RequestMapping("/view/stage")
    @ResponseBody
    public CommonReturnType handShake() {
        return CommonReturnType.create(null);
    }
}
