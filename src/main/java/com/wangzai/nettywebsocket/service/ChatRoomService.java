package com.wangzai.nettywebsocket.service;

import com.wangzai.nettywebsocket.pojo.ChatRoom;

public interface ChatRoomService {
    void create(ChatRoom chatRoom);

    Boolean entryRoom(Integer roomId);

    void exitRoom();

}
