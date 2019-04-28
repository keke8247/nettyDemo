package com.wdk.netty.time;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;


/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/18 15:09
 * @Since version 1.0.0
 */
public class TimeClient {

    //连接服务端
    public void connection(String host,int port){
        Bootstrap bootstrap = new Bootstrap();

        EventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group);

        bootstrap.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecode",new LineBasedFrameDecoder(1024));
                pipeline.addLast("stringDecode",new StringDecoder());
                pipeline.addLast("clientHandler",new TimeClientHandler());
            }
        });

        try {
            ChannelFuture future = bootstrap.connect(host,port).sync();
            System.out.println("netty 客户端连接完成...:");

            //阻塞线程  等待客户端链路关闭 再往下执行
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
          group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new TimeClient().connection("127.0.0.1",10086);
    }
}
