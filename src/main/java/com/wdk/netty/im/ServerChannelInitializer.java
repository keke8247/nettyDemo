package com.wdk.netty.im;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/28 15:53
 * @Since version 1.0.0
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        System.out.println("客户端"+ch.remoteAddress()+"连接....");

        ChannelPipeline cp = ch.pipeline();

//        cp.addLast("frame",new DelimiterBasedFrameDecoder(64, Delimiters.lineDelimiter()));
        cp.addLast("decode", new StringDecoder());
        cp.addLast("encode", new StringEncoder());
        cp.addLast("serverHandler", new ImServerHandler());
    }
}
