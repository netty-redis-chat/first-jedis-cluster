package com.wangzai.nettywebsocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author:   wbx
 * @email:    wbx123450@163.com
 * @date:     2019/4/21-15:04
 * @module:   []
 * @describe: [websocket服务器:
 *                 1. bind绑定服务器监听请求端口
 *                 2. handler负责处理channel的数据
 *                 3. channel加入时, 添加进group容器
 *                 4. channel关闭时, 从group容器删除
 *                 5. 每次channelRead0后, 转发group容器所有channel
 *                 6. channelReadComplete后, flush写后
 *                 7. exceptionCaught异常处理, 关闭channel
 *                 8. sendResponse, 发送错误信息]
 * @version:  v1.0
 */
public class WebSocket {

    public static void main(String[] args) {
        System.out.println("hello");
        new WebSocket().bind(8080);
    }

    private final static AttributeKey<String> roomKey = AttributeKey.newInstance("uri");
    private final static String roomPreix = "ws://localhost:8080";
    private final ConcurrentHashMap<String, ChannelGroup> channelGroups = new ConcurrentHashMap<>();
    private final Logger log = Logger.getLogger(WebSocketHandler.class.getName());

    public void bind(int port){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap start = new ServerBootstrap();
            start.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {


                            /**
                             * 设置编码器
                             */
                            ch.pipeline().addLast("http-code", new HttpServerCodec());
                            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("http-chunk", new ChunkedWriteHandler());

                            //添加事件处理
                            ch.pipeline().addLast(new WebSocketHandler());
                        }
                    });

            ChannelFuture future = start.bind(port).sync();

            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * WebSocket基于http1.1, 所以handler应该基于WebSocketFrame, 但也有可能是传统的FullHttpReXXX
     */
    class WebSocketHandler extends SimpleChannelInboundHandler<Object>{

        private WebSocketServerHandshaker handshaker = null;

        /**
         * channel通道建立时, 执行handlerAdded
         * @param ctx
         * @throws Exception
         */
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//            Channel incoming = ctx.channel();
//            for (Channel channel : group) {
//                channel.writeAndFlush(incoming.remoteAddress() + " 加入\n");
//            }
//            group.add(ctx.channel());
        }

        /**
         * channel通道关闭前, 执行handlerRemoved
         * @param ctx
         * @throws Exception
         */
        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            Channel incoming = ctx.channel();
            String room = ctx.channel().attr(roomKey).get();
            ChannelGroup group = channelGroups.get(room);

            if (group.size() == 0){
                channelGroups.remove(room);
            } else {
//                for (Channel channel : group) {
//                    channel.writeAndFlush(new TextWebSocketFrame(incoming.remoteAddress() + " 离开\n"));
//                }
                group.remove(ctx.channel());
            }
        }


        /**
         * 读取通道
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {


            if (msg instanceof FullHttpRequest){

                System.out.println("====== " + ((FullHttpRequest) msg) + " ==========");
               // log.info(String.format("调试: %s", ((FullHttpRequest) msg).toString()));

                String uri = ((FullHttpRequest) msg).uri();
                System.out.println("房间号:" + uri);
               // log.info(String.format("建立连接: come http %s %s", ctx.channel().remoteAddress(),uri));
                if (!channelGroups.containsKey(uri)){
                    channelGroups.put(uri, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));

                    Enumeration<String> keys = channelGroups.keys();
                    String roomList = "";
                    while (keys.hasMoreElements()){
                        roomList += keys.nextElement() + "  -  ";
                    }
                   // log.info(String.format("房间列表: %s", roomList));
                }

                channelGroups.get(uri).add(ctx.channel());
                ctx.channel().attr(roomKey).set(uri);
                handlerHttpRequest(ctx, (FullHttpRequest)msg);
            }

            else if (msg instanceof WebSocketFrame){
                handlerWebSocketFrame(ctx, (WebSocketFrame)msg);
            }
        }


        /**
         * 读取通道完毕后执行
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        /**
         * websocket第一次基于传统http1.1, 所以需要处理FullHttpRequest
         * @param ctx
         * @param req
         */
        private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req){
            if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))){
                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                return;
            }


            WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory(
                 roomPreix + ctx.channel().attr(roomKey).get(), null, false
            );
            handshaker = webSocketServerHandshakerFactory.newHandshaker(req);
            if (handshaker == null){
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), req);
            }
        }

        /**
         * 基于websocket, channelGroup服务器主动发送消息
         * CloseWebSocketFrame, PingWebSocketFrame, TextWebSocketFrame
         * @param ctx
         * @param frame
         */
        private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
            //1. 接收到结束请求
            if (frame instanceof CloseWebSocketFrame){
               // log.info(String.format("请求结束: end webSocket %s", (CloseWebSocketFrame) frame.retain()).toString());
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
                return;
            }

            //2. 接受到ping请求
            if (frame instanceof PingWebSocketFrame){
              //  log.info(String.format("ping命令: ping frame %s", frame.content().retain()).toString());
                ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
                return;
            }

            //3. 如果不是文本, 则异常
            if (!(frame instanceof TextWebSocketFrame)){
                throw new UnsupportedOperationException(String.format("%s not support ", frame.getClass().getName()));
            }

            //4. 返回应答信息
            String request = ((TextWebSocketFrame) frame).text();
//            if (log.isLoggable(Level.INFO)){
//                log.info(String.format("%s receive %s", ctx.channel().toString(), request));
//            }


            //ctx.channel().write(new TextWebSocketFrame(request + ", netty测试: " +  new Date().toString() + "\n"));


            Channel incoming = ctx.channel();
            String uri = (String) incoming.attr(roomKey).get();
            ChannelGroup group = channelGroups.get(uri);
            for (Channel channel : group) {
                if (channel != incoming) {
                    channel.writeAndFlush(new TextWebSocketFrame("[" + incoming.remoteAddress() + "]:   " + request));
                } else {
                    channel.writeAndFlush(new TextWebSocketFrame("[you]:   " + request));
                }
            }

            String authorUri = "/view" + uri;
            if (channelGroups.containsKey(authorUri)) {
                ChannelGroup authorGroup = channelGroups.get(authorUri);
                for (Channel channel : authorGroup){
                    channel.writeAndFlush(new TextWebSocketFrame("[" + "表演: " + incoming.remoteAddress() + "]:   " + request));
                }
            }

        }

        /**
         * 发送错误信息
         * @param ctx
         * @param req
         * @param resp
         */
        private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse resp){
            if (resp.status().code() != 200){
                ByteBuf buf = Unpooled.copiedBuffer(resp.status().toString(), CharsetUtil.UTF_8);
                resp.content().writeBytes(buf);
                buf.release();
                HttpUtil.setContentLength(resp, resp.content().readableBytes());
            }

            ChannelFuture future = ctx.channel().writeAndFlush(resp);
            if (!HttpUtil.isKeepAlive(req) || resp.status().code() != 200){
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

}

