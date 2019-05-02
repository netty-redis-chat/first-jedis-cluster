package com.wangzai.nettywebsocket.redismq.websocket.springboot;

import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

@Service
@ServerEndpoint("/ws")
public class WebSocketServerEndpoint {
    private static AtomicLong online = new AtomicLong(0);
    
    private static CopyOnWriteArrayList<WebSocketServerEndpoint>
            webSocketSet = new CopyOnWriteArrayList<>();
    
    Session session;

    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
    
    @OnOpen
    public void onOpen(Session session) {
        this.session=session;
        webSocketSet.add(this);
        online.incrementAndGet();
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        online.decrementAndGet();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        for (WebSocketServerEndpoint client : webSocketSet) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
