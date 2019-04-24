package com.wangzai.nettywebsocket.service.impl;

import com.wangzai.nettywebsocket.mapper.ChatRoomMapper;
import com.wangzai.nettywebsocket.pojo.ChatRoom;
import com.wangzai.nettywebsocket.service.ChatRoomMySQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomMySQLServiceImpl implements ChatRoomMySQLService {
    @Autowired
    ChatRoomMapper chatRoomMapper;


    @Override
    public Integer insertChatRoom(ChatRoom chatRoom) {
        return chatRoomMapper.insertChatRoom(chatRoom);
    }

    @Override
    public List<ChatRoom> list() {
        return chatRoomMapper.list();
    }

    @Override
    public List<ChatRoom> search(String keyword) {
        return chatRoomMapper.search(keyword);
    }

    @Override
    public void deleteChatRoom(Integer id) {
        chatRoomMapper.deleteChatRoom(id);
    }
}
