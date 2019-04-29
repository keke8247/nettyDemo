package com.wdk.netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/29 10:42
 * @Since version 1.0.0
 */
public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline cp = ch.pipeline();

        //将请求或者应答消息 编码或解码为Http消息.
        cp.addLast("http-codec",new HttpServerCodec());

        //将Http消息的多个部分组合成一条完整的Http消息.
        cp.addLast("aggregator",new HttpObjectAggregator(65536));

        //向客户端发送HTML5文件,它主要用于支持服务器和浏览器端进行WebSocket通信
        cp.addLast("http-chunked",new ChunkedWriteHandler());

        //WebSocketServerHandler
        cp.addLast("websocketServerHandler",new WebSocketServerHandler());
    }
}
