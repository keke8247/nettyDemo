package com.wdk.netty.im;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/28 15:41
 * @Since version 1.0.0
 */
public class ImServer {

    public void startServer(int port){
        ServerBootstrap bootstrap = new ServerBootstrap();

        EventLoopGroup baseGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        //使用Reactor主从线程模型
        bootstrap.group(baseGroup,workGroup);

        bootstrap.channel(NioServerSocketChannel.class);

        bootstrap.option(ChannelOption.SO_BACKLOG,1024);

        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);

        bootstrap.childHandler(new ServerChannelInitializer());

        try {
            ChannelFuture cf = bootstrap.bind(port).sync();

            System.out.println("服务器启动完成.....");

            //阻塞当前线程 直到server端口关闭之后 继续执行.保持Server在线.
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            baseGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ImServer().startServer(10086);
    }
}
