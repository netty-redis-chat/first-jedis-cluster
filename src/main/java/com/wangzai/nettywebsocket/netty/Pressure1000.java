package com.wangzai.nettywebsocket.netty;

import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * netty:9743
 */
public class Pressure1000 {
    public static CopyOnWriteArrayList<Client> clients = new CopyOnWriteArrayList<>();
    public static ExecutorService threadPool = Executors.newFixedThreadPool(100);


    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            Client client = new Client();
            client.connect();
            clients.add(client);

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Client client = new Client();
//                    client.connect();
//                    clients.add(client);
//                }
//            }).start();
        }


//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (!Thread.currentThread().isInterrupted()) {
//                    for (Client client : clients) {
//                        threadPool.execute(new PressureTestTask(client));
//                    }
//                }
//            }
//        });
//        thread.start();

        for (Client client : clients) {
            threadPool.execute(new PressureTestTask10(client));
        }


        Thread.sleep(10000);
        for (Client client : clients) {
            client.channel.writeAndFlush(new CloseWebSocketFrame());
//            client.future.channel().closeFuture().sync();
            client.group.shutdownGracefully();
        }
        threadPool.shutdownNow();
    }
}


class PressureTestTask10 implements Runnable {

    Client client;

    public PressureTestTask10(Client client) {
        this.client = client;
    }

    @Override
    public void run() {

//        for (int i = 0; i < 1; i ++){
        for (int i = 0; i < 10; i ++){
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
            client.channel.writeAndFlush(new TextWebSocketFrame(sdf.format(new Date())));
        }
        client.channel.writeAndFlush(new CloseWebSocketFrame());
    }
}
