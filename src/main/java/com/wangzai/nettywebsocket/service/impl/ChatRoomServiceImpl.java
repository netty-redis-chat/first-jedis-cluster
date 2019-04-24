package com.wangzai.nettywebsocket.service.impl;

import com.wangzai.nettywebsocket.pojo.ChatRoom;
import com.wangzai.nettywebsocket.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    private static String redisChatRoomPrefix="chatroom:";

    @Autowired
    ChatRoomMySQLServiceImpl chatRoomMySQLService;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    HttpSession httpSession;

    @Override
    public void create(ChatRoom chatRoom) {
        chatRoomMySQLService.insertChatRoom(chatRoom);
//        redisTemplate.execute(new SessionCallback(){
//            @Override
//            public Object execute(RedisOperations redisOperations) throws DataAccessException {
//                redisOperations.opsForSet().add(redisChatRoomPrefix + chatRoom.getId(), httpSession.getId());
//                redisOperations.opsForSet().add(redisChatRoomPrefix + chatRoom.getId(), (String) httpSession.getAttribute("username"));
////                redisOperations.opsForSet().add(redisChatRoomPrefix + chatRoom.getId(), (Object) "izaya");
//                return null;
//            }
//        });
        redisTemplate.opsForSet().add(redisChatRoomPrefix + chatRoom.getId(), httpSession.getId());
        redisTemplate.opsForSet().add(redisChatRoomPrefix + chatRoom.getId(), (String) httpSession.getAttribute("username"));
//        redisTemplate.opsForSet().add(redisChatRoomPrefix + chatRoom.getId(), "izaya");
        httpSession.setAttribute("room",chatRoom.getId());
    }

    public Boolean entryRoom(Integer roomId) {
        if (!redisTemplate.hasKey(redisChatRoomPrefix + roomId)) {
            return false;
        }
        if (redisTemplate.opsForSet().isMember(redisChatRoomPrefix + roomId, httpSession.getId())) {
            return true;
        }
        if (redisTemplate.opsForSet().isMember(redisChatRoomPrefix + roomId,  (String) httpSession.getAttribute("username")) ) {
            return false;
        }
//        redisTemplate.execute(new SessionCallback(){
//            @Override
//            public Object execute(RedisOperations redisOperations) throws DataAccessException {
//                redisOperations.opsForSet().add(redisChatRoomPrefix + roomId, httpSession.getId());
//                redisOperations.opsForSet().add(redisChatRoomPrefix + roomId,  (String) httpSession.getAttribute("username"));
//                return null;
//            }
//        });
        redisTemplate.opsForSet().add(redisChatRoomPrefix + roomId, httpSession.getId());
        redisTemplate.opsForSet().add(redisChatRoomPrefix + roomId,  (String) httpSession.getAttribute("username"));
        httpSession.setAttribute("room",roomId);
        return true;
    }

    public void exitRoom() {
        Integer roomId = (Integer) httpSession.getAttribute("room");
//        redisTemplate.execute(new SessionCallback(){
//            @Override
//            public Object execute(RedisOperations redisOperations) throws DataAccessException {
//                redisOperations.opsForSet().remove(redisChatRoomPrefix + roomId, httpSession.getId());
//                redisOperations.opsForSet().remove(redisChatRoomPrefix + roomId,  (String) httpSession.getAttribute("username"));
//                return null;
//            }
//        });
        redisTemplate.opsForSet().remove(redisChatRoomPrefix + roomId, httpSession.getId());
        redisTemplate.opsForSet().remove(redisChatRoomPrefix + roomId,  (String) httpSession.getAttribute("username"));
        httpSession.removeAttribute("room");
        if (redisTemplate.opsForSet().size(redisChatRoomPrefix + roomId) == 0) {
            redisTemplate.delete(redisChatRoomPrefix + roomId);
            chatRoomMySQLService.deleteChatRoom(roomId);
        }
    }
}
