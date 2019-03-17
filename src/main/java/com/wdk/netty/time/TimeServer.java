package com.wdk.netty.time;

import com.wdk.netty.discard.DiscardServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {
    public void run(int port){
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup(3);

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(boss,worker);

        serverBootstrap.channel(NioServerSocketChannel.class);

        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline cp = ch.pipeline();
                cp.addLast("时间",new TimeServerHandler());
            }
        });

        serverBootstrap.option(ChannelOption.SO_BACKLOG,128);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);

        try {
            ChannelFuture cf = serverBootstrap.bind(port).sync();

            System.out.println("server 已成功启动......");
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new TimeServer().run(8080);
    }
}
