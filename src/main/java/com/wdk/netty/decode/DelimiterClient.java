package com.wdk.netty.decode;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/19 10:14
 * @Since version 1.0.0
 */
public class DelimiterClient {
    public void connect(String host,int port){
        //启动辅助类
        Bootstrap bootstrap = new Bootstrap();

        //线程组
        EventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group);

        bootstrap.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ByteBuf byteBuf = Unpooled.copiedBuffer("$_".getBytes());
                ch.pipeline().addLast("delimiterDecoder",new DelimiterBasedFrameDecoder(1024,byteBuf));
//                ch.pipeline().addLast("fixedDecoder",new FixedLengthFrameDecoder(20));
                ch.pipeline().addLast("stringDecoder",new StringDecoder());
                ch.pipeline().addLast("clientHandler",new DelimiterClientHandler());
            }
        });
        try {
            //连接
            ChannelFuture cf = bootstrap.connect(host,port).sync();

            System.out.println("连接完成....");

            //设置同步阻塞 直到当前连接关闭
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new DelimiterClient().connect("127.0.0.1",10086);
    }
}
