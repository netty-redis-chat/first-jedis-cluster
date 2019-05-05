package com.wangzai.nettywebsocket.redismq.mq;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import javax.websocket.Session;
import java.io.IOException;

public class RedisMessageListener implements MessageListener {
    Session session;

    public RedisMessageListener(Session session) {
        this.session = session;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String body = new String(message.getBody());
        String topic = new String(bytes);
        try {
            session.getBasicRemote().sendText(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
