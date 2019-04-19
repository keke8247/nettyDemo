package com.wdk.netty.danmu;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * @Description
 * @Author wangdk
 * @CreateTime 2019-02-26 21:27
 * @Since version 1.0.0
 */
public class WebSocketDanmuServerInitializer extends ChannelInitializer<NioSocketChannel> {
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        ChannelPipeline cp = nioSocketChannel.pipeline();

        cp.addLast("http-decoder",new HttpRequestDecoder());
        cp.addLast("http-aggregator",new HttpObjectAggregator(65535));
        cp.addLast("http-encoder",new HttpResponseEncoder());
        cp.addLast("http-request",new HttpRequestHandler("/ws"));

        //实现websocket协议的编码与解码
        cp.addLast("WebSocket-protocol",new WebSocketServerProtocolHandler("/ws"));

        //实现弹幕发送业务
        cp.addLast("WebSocket-request",new TextWebSocketFrameHandler());

    }
}
