package com.wangzai.nettywebsocket.controller;

import com.wangzai.nettywebsocket.error.BusinessException;
import com.wangzai.nettywebsocket.pojo.ChatRoom;
import com.wangzai.nettywebsocket.response.CommonReturnType;
import com.wangzai.nettywebsocket.service.ChatRoomMySQLService;
import com.wangzai.nettywebsocket.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


@RequestMapping("/room")
@Controller
public class RoomController extends BaseController {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    ChatRoomMySQLService chatRoomMySQLService;

    @Autowired
    ChatRoomService chatRoomService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/list")
    public String listRoom(Model model) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        List<ChatRoom> list = chatRoomMySQLService.list();
        model.addAttribute("roomlist", list);

        return "home/roomlist";
    }

    @RequestMapping("/createpage")
    public String createChatRoom() {

        return "/chatroom/create";
    }

    @RequestMapping("/create")
    @ResponseBody
    public CommonReturnType createChatRoom(String roomName, Integer capacity) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(roomName);
        chatRoom.setCapacity(capacity);

        chatRoomService.create(chatRoom);

        return CommonReturnType.create("/room/" + chatRoom.getId());
    }

    @RequestMapping("/{room_id}")
    public String room(@PathVariable Integer room_id, HttpServletResponse response) throws IOException, BusinessException {
        if (!chatRoomService.entryRoom(room_id)) {
            return "redirect:/room/list";
//            throw new  BusinessException(EmBusinessErr.UNKNOWN_ERROR, "房间中已有该用户名");
        }

        return "/chatroom/common";
    }
//
//    @RequestMapping("/entry/{room_id}")
//    public String chatroom(@PathVariable Integer room_id) {
//        return "/chatroom/common";
//    }

    @RequestMapping("/exit")
    @ResponseBody
    public CommonReturnType room() {
        chatRoomService.exitRoom();
//        HttpSession session = httpServletRequest.getSession();
//        String room_id = (String) session.getAttribute("room_id");
//        redisTemplate.opsForSet().remove("chrm:"+room_id, (String) session.getAttribute("username"));
//        session.removeAttribute("room");
        return CommonReturnType.create(null);
    }
}
