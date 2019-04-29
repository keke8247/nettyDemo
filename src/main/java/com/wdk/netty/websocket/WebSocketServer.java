package com.wdk.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description
 * 使用WebSocket协议 和浏览器通信
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/29 10:36
 * @Since version 1.0.0
 */
public class WebSocketServer {
    public void startServer(int port){
        ServerBootstrap bootstrap = new ServerBootstrap();

        //使用主从Reactor线程模型
        EventLoopGroup baseGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        bootstrap.group(baseGroup,workGroup);

        bootstrap.channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG,128);

        bootstrap.childHandler(new WebSocketInitializer());

        try {
            ChannelFuture cf = bootstrap.bind(port).sync();

            System.out.println("WebSocketServer start success...port:"+port);

            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            baseGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new WebSocketServer().startServer(8080);
    }
}
