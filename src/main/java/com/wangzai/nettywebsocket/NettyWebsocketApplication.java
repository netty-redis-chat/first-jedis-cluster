package com.wangzai.nettywebsocket;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
@MapperScan("com.wangzai.nettywebsocket.mapper")
public class NettyWebsocketApplication {

	static Logger log = LoggerFactory.getLogger(NettyWebsocketApplication.class);
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}


	public static void main(String[] args) throws Exception {
//		SpringApplication.run(NettyWebsocketApplication.class, args);
//		ServerBootstrap serverBootstrap = context.getBean(ServerBootstrap.class);
//		serverBootstrap.bind(8888).sync();

        ConfigurableApplicationContext context = SpringApplication.run(NettyWebsocketApplication.class, args);

//		NettyServer nettyServer = context.getBean(NettyServer.class);
//		nettyServer.start();
		
		//log.info("--------before");
        //new Thread(()->{
		//	new WebSocket().bind(8888);
		//}).start();
		//log.info("-----------------------");
        //new WebSocket().bind(8889);
		//log.info("--------after");
	}
}
