package com.wdk.netty.fileHttp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Description
 * Http文件服务器
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/22 11:00
 * @Since version 1.0.0
 */
public class HttpFileServer {

    private static final String DEFAULT_URL = "/";

    public void startServer(int port,final String path){
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        EventLoopGroup baseGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        serverBootstrap.group(baseGroup,workGroup);

        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("httpDecoder",new HttpRequestDecoder())
                        .addLast("httpAggregator",new HttpObjectAggregator(65536))
                        .addLast("httpEncoder",new HttpResponseEncoder())
                        .addLast("httpChunked",new ChunkedWriteHandler())
                        .addLast("fileServerHandler",new HttpFileServerHandler(path));
            }
        });

        try {
            ChannelFuture cf = serverBootstrap.bind(port).sync();

            System.out.println("文件服务器启动完成....."+port);

            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            baseGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new HttpFileServer().startServer(10086,DEFAULT_URL);
    }
}
