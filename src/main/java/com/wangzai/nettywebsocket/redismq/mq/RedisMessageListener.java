package com.wangzai.nettywebsocket.redismq.mq;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;

@Component
public class RedisMessageListener implements MessageListener {
    private Session session;
    
    /**
     * 
     * @param message
     * @param bytes 频道名称
     */
    @Override
    public void onMessage(Message message, byte[] bytes) {
        String body = new String(message.getBody());
        String topic = new String(bytes);
        try {
            session.getBasicRemote().sendText("body");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
