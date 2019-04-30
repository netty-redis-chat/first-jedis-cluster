package com.wangzai.nettywebsocket.service;

import com.wangzai.nettywebsocket.pojo.ChatRoom;
import com.wangzai.nettywebsocket.pojo.User;

import java.util.List;

public interface RoomService {

    void create(ChatRoom chatRoom, User user);

    List<ChatRoom> roomlist();
}
