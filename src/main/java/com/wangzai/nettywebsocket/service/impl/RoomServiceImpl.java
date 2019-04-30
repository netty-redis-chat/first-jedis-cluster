package com.wangzai.nettywebsocket.service.impl;

import com.google.gson.Gson;
import com.wangzai.nettywebsocket.pojo.ChatRoom;
import com.wangzai.nettywebsocket.pojo.User;
import com.wangzai.nettywebsocket.redis.RedisCluster;
import com.wangzai.nettywebsocket.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RoomServiceImpl implements RoomService {
    @Autowired
    private JedisCluster jedisCluster;

    @Override
    public void create(ChatRoom chatRoom, User user) {

        JedisCluster jedisCluster = RedisCluster.getJedisClusterCon();
        Gson gson = new Gson();
        String roomjson = gson.toJson(chatRoom, ChatRoom.class);
        jedisCluster.sadd("chatrooms", "chrm:"+chatRoom.getId(),roomjson);
        jedisCluster.sadd("chrm:"+chatRoom.getId(),"usr:"+user.getId());
        jedisCluster.set("usr:" + user.getId(), user.getUsername());

    }

//    public String createChat(Jedis conn, String sender, Set<String> recipients, String message) {
//        //启动的时候redis里是没有ids:chat:这个键的
//        //自增之后返回1
//        String chatId = String.valueOf(conn.incr("ids:chat:"));
//        return createChat(conn, sender, recipients, message, chatId);
//    }
//
//
//    /**
//     *
//     * @param conn
//     * @param sender 发送消息的人
//     * @param recipients 接受消息的人
//     * @param message 待发送的消息
//     * @param chatId 聊天室的编号
//     * @return
//     */
//    public String createChat( Jedis conn, String sender,
//                              Set<String> recipients, String message, String chatId){
//        //自己发的消息 自己也能接受到
//        recipients.add(sender);
//
//
//        Transaction trans = conn.multi();
//        for (String recipient : recipients){
//            //聊天室的成员 最开始时 都阅读的是0号信息
//            trans.zadd("chat:" + chatId, 0, recipient);
//            //记录每个人参加的聊天室
//            trans.zadd("seen:" + recipient, 0, chatId);
//        }
//        trans.exec();
//
//
//        return sendMessage(conn, chatId, sender, message);
//    }


//    public String sendMessage(Jedis conn, String chatId, String sender, String message) {
//
//        //锁住聊天室 为啥? 人员变动了咋办
//        //这个acquireLock见上一章
//        String identifier = acquireLock(conn, "chat:" + chatId);
//        if (identifier == null){
//            throw new RuntimeException("Couldn't get the lock");
//        }
//        try {
//            //给要发布的消息设定一个最新的编号  第一次时 返回的是1
//            long messageId = conn.incr("ids:" + chatId);
//            HashMap<String,Object> values = new HashMap<String,Object>();
//            values.put("id", messageId);
//            values.put("ts", System.currentTimeMillis());
//            values.put("sender", sender);
//            values.put("message", message);
//            String packed = new Gson().toJson(values);
//
//            //某个聊天室的消息列表
//            //最旧的消息----消息json
//            //默认的zset是按照score的值从小到大排序
//            conn.zadd("msgs:" + chatId, messageId, packed);
//        }finally{
//            releaseLock(conn, "chat:" + chatId, identifier);
//        }
//        return chatId;
//    }

    @Override
    public List<ChatRoom> roomlist() {
        JedisCluster jedisCluster = RedisCluster.getJedisClusterCon();
        Gson gson = new Gson();
        List<ChatRoom> chatRoomList = new ArrayList<>();
        Set<String> chatrooms = jedisCluster.smembers("chatrooms");
        for (String str : chatrooms) {
            chatRoomList.add(gson.fromJson(str, ChatRoom.class));
        }
        return chatRoomList;
    }
}
