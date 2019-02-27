package com.wdk.netty.danmu;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @Description
 * @Author wangdk
 * @CreateTime 2019-02-26 21:33
 * @Since version 1.0.0
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //维护一个客户连接池
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        Channel incoming = channelHandlerContext.channel();
        for(Channel channel : channels){
            if(channel != incoming){
                channel.writeAndFlush(new TextWebSocketFrame(textWebSocketFrame.text()));
            }else{
                channel.writeAndFlush(new TextWebSocketFrame("我发送的："+textWebSocketFrame.text()));
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx){
        Channel incoming = ctx.channel();

        //往channel组里面发一条消息  （向所有人发一条消息）
        channels.writeAndFlush(new TextWebSocketFrame("[Server]-"+incoming.remoteAddress()+"加入"));
        channels.add(incoming);
        System.out.println("client:"+incoming.remoteAddress()+"加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        channels.writeAndFlush(new TextWebSocketFrame("[Server]-"+incoming.remoteAddress()+"离开"));
        System.out.println("client:"+incoming.remoteAddress()+"离开");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("client:"+incoming.remoteAddress()+"在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("client:"+incoming.remoteAddress()+"掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("client:"+incoming.remoteAddress()+"异常");

        //当出现异常情况  关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
