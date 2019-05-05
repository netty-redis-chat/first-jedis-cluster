package com.wangzai.nettywebsocket.redismq.websocket.withredis;

import com.wangzai.nettywebsocket.redismq.mq.RedisMessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.atomic.AtomicLong;

@Service
@ServerEndpoint("/ws")
public class WebSocketEndpoint {
    private static final String topic = "ws";
    private static AtomicLong online = new AtomicLong(0);

    private StringRedisTemplate redisTampate = SpringUtils.getBean(StringRedisTemplate.class);
    private RedisMessageListenerContainer container = SpringUtils.getBean(RedisMessageListenerContainer.class);
    private IpConfiguration ipConfiguration = SpringUtils.getBean(IpConfiguration.class);

    private Session session;
    private RedisMessageListener listener;
    
    @OnOpen
    public void onOpen(Session session) {
        RedisMessageListener listener = new RedisMessageListener(session);
        container.addMessageListener(listener,new ChannelTopic(topic));
        this.listener = listener;
        
        online.incrementAndGet();
    }

    @OnClose
    public void onClose() {
        container.removeMessageListener(listener);
        online.decrementAndGet();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        redisTampate.convertAndSend(topic,message+" from "+ipConfiguration.getPort()+" online "+online);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
