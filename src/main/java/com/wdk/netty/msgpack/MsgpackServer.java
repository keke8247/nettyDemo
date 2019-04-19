package com.wdk.netty.msgpack;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/19 14:50
 * @Since version 1.0.0
 */
public class MsgpackServer {

    public void startServer(int port){
        //启动辅助类
        ServerBootstrap bootstrap = new ServerBootstrap();

        //监听分发线程组
        EventLoopGroup baseGroup = new NioEventLoopGroup();

        //工作线程组
        EventLoopGroup workGroup = new NioEventLoopGroup();

        bootstrap.group(baseGroup,workGroup);

        bootstrap.channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG,1024);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("msgDecoder",new MsgpackDecoder())
                .addLast("msgEncoder",new MsgpackEncoder())
                .addLast("serverHandler",new MsgpackServerHandler());
            }
        });

        try {
            ChannelFuture cf = bootstrap.bind(port).sync();

            System.out.println("MsgpackServer启动完成....端口号:"+port);

            //阻塞当前线程.直到服务单端口关闭
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            baseGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new MsgpackServer().startServer(10086);
    }
}
