package com.wdk.netty.im;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/18 14:43
 * @Since version 1.0.0
 */
public class TimeServer {

    //启动netty 服务
    public void startNetty(int port){
        //启动辅助类
        ServerBootstrap bootstrap = new ServerBootstrap();

        //线程组
        EventLoopGroup baseLoop = new NioEventLoopGroup(); //用于监听网络事件的线程
        EventLoopGroup workLoop = new NioEventLoopGroup(); //用于处理channel事务的线程组

        bootstrap.group(baseLoop,workLoop);
        bootstrap.channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG,1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //通过 LineBasedFrameDecoder 和 StringDecoder处理拆包和粘包的问题
                        pipeline.addLast("frameDecode",new LineBasedFrameDecoder(1024));
                        pipeline.addLast("stringDecode",new StringDecoder());
                        pipeline.addLast("timeHandler",new TimeServerHandler());
                    }
                });

        //启动服务
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.bind(port).sync();

            System.out.println("netty 服务启动完成...端口:"+port);

            //阻塞线程 直到服务端端口关闭 再往下执行
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //优雅的释放资源
            baseLoop.shutdownGracefully();
            workLoop.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new TimeServer().startNetty(10086);
    }
}
