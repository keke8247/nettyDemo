package com.wdk.netty.im;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/18 14:49
 * @Since version 1.0.0
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter{
    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //加入 拆包 粘包Handler以前的处理方式 start
        /**
         ByteBuf byteBuf = (ByteBuf) msg;//把客户端发送过来的信息强转成netty 支持的ByteBuf

         byte[] req = new byte[byteBuf.readableBytes()];

         byteBuf.readBytes(req);//读取数据

         String reqBody = new String(req,"UTF-8");//客户端发送的数据

         System.out.println("服务端接收到:"+reqBody);

         String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(reqBody)?new Date(System.currentTimeMillis()).toString() : "BAD ERROR";

         //把currentTime 返回给客户端

         ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());

         ctx.write(resp);
         */
        //加入 拆包 粘包Handler以前的处理方式 end


        //加入 拆包 粘包Handler以后的处理方式 start
        String reqBody = (String) msg;

        System.out.println("服务端接收到:"+reqBody+"请求次数"+ ++counter);

        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(reqBody)?new Date(System.currentTimeMillis()).toString() : "BAD ERROR";

        //把currentTime 返回给客户端

        ByteBuf resp = Unpooled.copiedBuffer((currentTime+System.getProperty("line.separator")).getBytes());

        ctx.write(resp);
        //加入 拆包 粘包Handler以后的处理方式 end
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
