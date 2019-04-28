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
 * @CreatTime 2019/4/28 16:25
 * @Since version 1.0.0
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast("frame",new DelimiterBasedFrameDecoder(64, Delimiters.lineDelimiter()));
        pipeline.addLast("decode",new StringDecoder());//解码器
        pipeline.addLast("encode",new StringEncoder());
        pipeline.addLast("handler",new ImClientHandler());
    }
}
