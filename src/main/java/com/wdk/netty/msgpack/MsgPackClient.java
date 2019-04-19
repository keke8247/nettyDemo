package com.wdk.netty.msgpack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/19 15:01
 * @Since version 1.0.0
 */
public class MsgPackClient {

    public void connect(String host,int port){
        Bootstrap bootstrap = new Bootstrap();

        EventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group);

        bootstrap.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535,0,2,0,2))
                        .addLast("msgDecoder",new MsgpackDecoder())
                        .addLast("frameEncoder",new LengthFieldPrepender(2))
                        .addLast("msgEncoder",new MsgpackEncoder())
                        .addLast("clientHandler",new MsgpackClientHandler());
            }
        });

        try {
            ChannelFuture cf = bootstrap.connect(host,port).sync();

            System.out.println("客户端连接成功");

            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MsgPackClient().connect("127.0.0.1",10086);
    }
}
