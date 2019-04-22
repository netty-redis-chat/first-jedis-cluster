package com.wangzai.nettywebsocket;

import com.wangzai.nettywebsocket.Server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NettyWebsocketApplication {


	public static void main(String[] args) throws Exception {
//		SpringApplication.run(NettyWebsocketApplication.class, args);
//		ServerBootstrap serverBootstrap = context.getBean(ServerBootstrap.class);
//		serverBootstrap.bind(8888).sync();

        ConfigurableApplicationContext context = SpringApplication.run(NettyWebsocketApplication.class, args);

		NettyServer nettyServer = context.getBean(NettyServer.class);
		nettyServer.start();

	}


}
