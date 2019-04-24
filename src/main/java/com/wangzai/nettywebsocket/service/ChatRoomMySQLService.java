package com.wangzai.nettywebsocket.service;

import com.wangzai.nettywebsocket.pojo.ChatRoom;

import java.util.List;

public interface ChatRoomMySQLService {
    Integer insertChatRoom(ChatRoom chatRoom);

    List<ChatRoom> list();

    List<ChatRoom> search(String keyword);

    void deleteChatRoom(Integer id);
}
