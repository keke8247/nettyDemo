package com.wdk.netty.msgpack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/19 15:05
 * @Since version 1.0.0
 */
public class MsgpackClientHandler extends SimpleChannelInboundHandler{
    private UserInfo[] userInfos(int num){
        UserInfo [] userInfos = new UserInfo[num];

        for(int i=0;i<num;i++){
            UserInfo info = new UserInfo();
            info.setAge(i);
            info.setName("this name is ABC---->"+i);

            userInfos[i] = info;
        }
        return userInfos;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UserInfo [] userInfos = userInfos(5);

        for(UserInfo info : userInfos){
            ctx.writeAndFlush(info);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client receive the msgpack message is "+msg);
    }
}

