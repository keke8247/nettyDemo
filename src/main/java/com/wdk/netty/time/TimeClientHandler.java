package com.wdk.netty.time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/18 15:15
 * @Since version 1.0.0
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter{
    private int  counter;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //构造发往服务端的数据

        ByteBuf byteBuf = null;

        for(int i=0;i<10;i++){
            byteBuf = Unpooled.copiedBuffer(("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes());
            ctx.writeAndFlush(byteBuf);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //加入 拆包 粘包Handler以前的处理方式 start
        /**
         ByteBuf byteBuf = (ByteBuf) msg;

         byte [] resp = new byte[byteBuf.readableBytes()];

         byteBuf.readBytes(resp);

         String respBody = new String(resp,"UTF-8");

         System.out.println("客户端接收到服务端的数据为:"+respBody);
         */
        //加入 拆包 粘包Handler以前的处理方式 end

        //加入 拆包 粘包Handler以后的处理方式 start
        String reqBody = (String) msg;

        System.out.println("客户端接收到服务端的数据为:"+reqBody+"接收次数"+ ++counter);
        //加入 拆包 粘包Handler以后的处理方式 end

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
