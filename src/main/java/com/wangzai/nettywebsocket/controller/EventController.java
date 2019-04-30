package com.wangzai.nettywebsocket.controller;

import com.wangzai.nettywebsocket.pojo.Event;
import com.wangzai.nettywebsocket.response.CommonReturnType;
import com.wangzai.nettywebsocket.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/event")
public class EventController extends BaseController {

    @Autowired
    EventService eventService;

    @RequestMapping("/createpage")
    public String publish() {
        return "/event/publish";
    }

    @RequestMapping("/create")
    @ResponseBody
    public CommonReturnType create(String name, String roles, String time) {
        Event event = new Event();
        event.setName(name);
        event.setRoles(roles);
        eventService.create(event);

        return CommonReturnType.create(null);
    }

    @RequestMapping("/getRole")
    public String getRole() {
        if (eventService.getRole() == true) {
            return "redirect:/event/stage";
        } else {
            return "redirect:/event/view";
        }
    }

    @RequestMapping("/stage")
    public String stage() {
        if (eventService.isRole()) {
            return "/event/stage";
        }else {
            return "redirect:/event/view";
        }
    }

    @RequestMapping("/view")
    public String view() {
        return "/event/audience";
    }
}
