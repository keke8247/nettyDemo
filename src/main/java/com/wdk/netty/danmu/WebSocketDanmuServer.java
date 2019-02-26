package com.wdk.netty.danmu;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description
 * 基于Netty实现弹幕系统
 * @Author wangdk
 * @CreateTime 2019-02-26 21:20
 * @Since version 1.0.0
 */
public class WebSocketDanmuServer {

    private int port;

    public WebSocketDanmuServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        EventLoopGroup baseGroup = new NioEventLoopGroup(2); //监听线程组
        EventLoopGroup workerGoup = new NioEventLoopGroup(3);//Io线程组

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(baseGroup,workerGoup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WebSocketDanmuServerInitializer())
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            System.out.println("弹幕系统启动了："+port);

            ChannelFuture cf = serverBootstrap.bind(port).sync();
            cf.channel().closeFuture().sync();

        }finally {
            baseGroup.shutdownGracefully();
            workerGoup.shutdownGracefully();
            System.out.println("弹幕系统出现异常，关闭服务。");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port;
        if(args.length>0){
            port = new Integer(args[0]);
        }else{
            port = 8080;
        }

        new WebSocketDanmuServer(port).run();
    }
}
