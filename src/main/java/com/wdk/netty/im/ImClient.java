package com.wdk.netty.im;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/28 16:22
 * @Since version 1.0.0
 */
public class ImClient {

    public void connectServer(String host,int port){
        Bootstrap bootstrap = new Bootstrap();

        EventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group);

        bootstrap.channel(NioSocketChannel.class);

//        bootstrap.option(ChannelOption.SO_BACKLOG,1024);

        bootstrap.handler(new ClientChannelInitializer());

        try {
            Channel channel = bootstrap.connect(host,port).sync().channel();

            System.out.println("客户端连接成功:"+channel.localAddress());

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while(true){
                try {
                    channel.writeAndFlush(reader.readLine() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ImClient().connectServer("127.0.0.1",10086);
    }
}
