package com.wangzai.nettywebsocket.controller;

import com.wangzai.nettywebsocket.error.BusinessException;
import com.wangzai.nettywebsocket.error.EmBusinessErr;
import com.wangzai.nettywebsocket.response.CommonReturnType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@RequestMapping("/user")
@Controller
public class LoginController extends BaseController{

    @Autowired
    private HttpServletRequest httpServletRequest;//LocalThread 解决单例问题

    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {BaseController.CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "username") String username) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(username)) {
            throw new BusinessException(EmBusinessErr.PARAMETER_VALIDATION_ERROR);
        }

        this.httpServletRequest.getSession().setAttribute("user",username);

        return CommonReturnType.create(null);
    }
}
