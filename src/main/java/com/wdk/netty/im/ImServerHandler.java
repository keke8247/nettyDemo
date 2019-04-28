package com.wdk.netty.im;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/28 16:05
 * @Since version 1.0.0
 */
public class ImServerHandler extends SimpleChannelInboundHandler<String>{

    private ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client:"+ctx.channel().remoteAddress()+"异常");
        //当出现异常情况  关闭连接
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //有人进入聊天室
        Channel inComming = ctx.channel();
        for(Channel ch : channels){
            if(inComming != ch){ //有人进来 向聊天室其他人发送一条信息
                ch.writeAndFlush("热烈欢迎"+inComming.remoteAddress()+"进入聊天室...");
            }
        }
        //把当前接入的Channel放入聊天室
        channels.add(inComming);

        System.out.println("当前聊天室人数:"+channels.size());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //有链接断开  离开聊天室
        Channel outChannel = ctx.channel();
        for(Channel ch : channels){
            if(outChannel != ch){ //有人离开聊天室 向聊天室其他人发送一条消息.
                ch.writeAndFlush("再见:"+ch.remoteAddress()+"...欢迎再来.");
            }
        }
        channels.remove(outChannel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel incoming = ctx.channel();
        for(Channel channel : channels){
            if(channel != incoming){
                channel.writeAndFlush(incoming.remoteAddress()+"say:"+msg);
            }else{
                channel.writeAndFlush("I say:"+msg);
            }
        }
    }
}
