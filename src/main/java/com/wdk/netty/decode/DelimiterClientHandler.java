package com.wdk.netty.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/19 10:21
 * @Since version 1.0.0
 */
public class DelimiterClientHandler extends SimpleChannelInboundHandler{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf req = null;

        for (int i=0;i<10;i++){
            req = Unpooled.copiedBuffer(("我是第"+i+"个消息$_").getBytes());
            ctx.writeAndFlush(req);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        String resp = (String) msg;

        System.out.println("服务端返回消息:"+resp);
    }
}
