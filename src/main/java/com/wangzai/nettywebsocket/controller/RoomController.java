package com.wangzai.nettywebsocket.controller;

import com.google.gson.Gson;
import com.wangzai.nettywebsocket.error.BusinessException;
import com.wangzai.nettywebsocket.pojo.ChatRoom;
import com.wangzai.nettywebsocket.response.CommonReturnType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RequestMapping("/room")
@Controller
public class RoomController extends BaseController {

    @Autowired
    RedisTemplate<String,String> redisTemplate;



    @Autowired
    HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/list")
    public String listRoom(Model model) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        Gson gson = new Gson();
        List<ChatRoom> roomlist = new ArrayList<>();
        for (String str : jedis.lrange("chatroom", 0, 9)) {
            roomlist.add(gson.fromJson(str, ChatRoom.class));
        }
        model.addAttribute("roomlist", roomlist);

        return "home/roomlist";
    }

    @RequestMapping("/createpage")
    public String createChatRoom() {

        return "/chatroom/create";
    }

    @RequestMapping("/create")
    @ResponseBody
    public CommonReturnType createChatRoom(String roomName, Integer capacity) {
        ListOperations opsForList = redisTemplate.opsForList();
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(roomName);
        chatRoom.setCapacity(capacity);
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        UUID uuid = UUID.randomUUID();
        chatRoom.setId(uuid.toString());
        String s = new Gson().toJson(chatRoom, ChatRoom.class);
        opsForList.leftPush("chatroom", s);

        return CommonReturnType.create("/room/" + chatRoom.getId());
    }

    @RequestMapping("/{room_id}")
    public String room(@PathVariable String room_id) {
        HttpSession session = httpServletRequest.getSession();
        String username = (String) session.getAttribute("username");
//        if (redisTemplate.opsForSet().isMember("chrm:"+room_id, username)) {
//            return "redirect:/room/list";
//        }
        redisTemplate.opsForSet().add("chrm:"+room_id, username);

        if (session.getAttribute("root") == null) {
            session.setAttribute("room", room_id);
        }


        return "/chatroom/common";
    }

    @RequestMapping("/exit")
    @ResponseBody
    public CommonReturnType room() {
        HttpSession session = httpServletRequest.getSession();
        String room_id = (String) session.getAttribute("room_id");
        redisTemplate.opsForSet().remove("chrm:"+room_id, (String) session.getAttribute("username"));
        session.removeAttribute("room");
        return CommonReturnType.create(null);
    }
}
