package com.wangzai.nettywebsocket.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

public class Client {

    public static void main(String[] args) {


        new Client().connect();
    }


    public void connect(){
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap start = new Bootstrap();
            start.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(65536));
                            ch.pipeline().addLast(new ChunkedWriteHandler());

                            ch.pipeline().addLast("WebSocketClientHandler", new WebSocketClientHandler());
                        }
                    });

            //执行handler, 首先执行handlerAdd
            ChannelFuture future = start.connect("localhost", 8080).sync();

            //等待handshakeFuture, 执行完handShake握手
            WebSocketClientHandler handler = (WebSocketClientHandler) future.channel().pipeline().get("WebSocketClientHandler");
            handler.handshakeFuture().sync();



            System.out.println("握手完成");
            future.channel().writeAndFlush(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/view/stage"));
            future.channel().writeAndFlush(new TextWebSocketFrame("你好"));

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
        //设置握手参数
        private WebSocketClientHandshaker handShaker;
        private ChannelPromise handshakeFuture;
        WebSocketClientHandler() throws URISyntaxException {
            URI webSocketURI = new URI("ws://localhost:8080/view/stage");
            HttpHeaders httpHeaders = new DefaultHttpHeaders();
            handShaker = WebSocketClientHandshakerFactory.newHandshaker(webSocketURI, WebSocketVersion.V13, (String)null, false, httpHeaders);
        }


        /**
         * 返回等待握手完成对象, 虽然可以在channelActive中执行, 但由于handler执行后
         * 就可能马上判断sync(), 导致空指针, 所以还是尽早定义更好
         * @param ctx
         */
        public void handlerAdded(ChannelHandlerContext ctx) {
            this.handshakeFuture = ctx.newPromise();
        }
        public ChannelFuture handshakeFuture() {
            return handshakeFuture;
        }


        /**
         * 通道连接时, 执行握手连接
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelActive握手: " + ctx.channel());
            handShaker.handshake(ctx.channel());
        }

        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            Channel ch = ctx.channel();
            FullHttpResponse response;

            //客户端发送req后需要接受一个resp
            if (!this.handShaker.isHandshakeComplete()) {
                try {
                    response = (FullHttpResponse) msg;

                    this.handShaker.finishHandshake(ch, response);

                    this.handshakeFuture.setSuccess();
                    System.out.println("连接结束: " + response.headers());
                } catch (WebSocketHandshakeException var7) {
                    FullHttpResponse res = (FullHttpResponse) msg;
                    String errorMsg = String.format("WebSocket Client failed to connect,status:%s,reason:%s", res.status(), res.content().toString(CharsetUtil.UTF_8));
                    this.handshakeFuture.setFailure(new Exception(errorMsg));
                }
            } else {

                WebSocketFrame frame = (WebSocketFrame) msg;
                if (frame instanceof TextWebSocketFrame) {
                    TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                    System.out.println(ctx.channel().remoteAddress() + ": " + ((TextWebSocketFrame) frame).text());
                } else {
                    ch.close();
                }

            }
        }
    }
}
