package com.wdk.netty.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/19 10:08
 * @Since version 1.0.0
 */
public class DelimiterServerHandler extends SimpleChannelInboundHandler{

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        String req = (String) msg; //msg已经通过 StringDecoder解码成字符串

        System.out.println("服务端接收到的客户端数据为:"+req);

        ByteBuf resp = Unpooled.copiedBuffer((req+":你好"+"$_").getBytes());

        ctx.writeAndFlush(resp);
    }
}
