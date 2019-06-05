package com.wangzai.nettywebsocket.mapper;

import com.wangzai.nettywebsocket.pojo.ChatRoom;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomMapper {
    Integer insertChatRoom(ChatRoom chatRoom);

    List<ChatRoom> list();

    List<ChatRoom> search(String keyword);

    void deleteChatRoom(Integer id);
    
}
