package com.wdk.netty.decode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @Description
 * 使用DelimiterBasedFrameDecode 完成以指定分隔符作为码流结束标志的消息的解码
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/19 9:57
 * @Since version 1.0.0
 */
public class DelimiterServer {
    public void startServer(int port){
        //启动辅助类
        ServerBootstrap bootstrap = new ServerBootstrap();

        //监听线程组
        EventLoopGroup baseGroup = new NioEventLoopGroup();

        //工作线程组
        EventLoopGroup workGrop = new NioEventLoopGroup();

        bootstrap.group(baseGroup,workGrop);

        bootstrap.channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG,1024);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ByteBuf byteBuf = Unpooled.copiedBuffer("$_".getBytes());

                //添加DelimiterBasedFrameDecoder解码器
                //解码器第一个参数 1024表示包的最大长度,如果到达最大长度还没有读到分隔符则抛异常.防止异常码流没有分隔符导致内存溢出.
                //解码器第二个参数 byteBuf 表示分隔符
                ch.pipeline().addLast("delimiterDecoder",new DelimiterBasedFrameDecoder(1024,byteBuf));
//                ch.pipeline().addLast("fixedDecoder",new FixedLengthFrameDecoder(20));//定长拆包 消息长度设置为20
                ch.pipeline().addLast("stringDecoder",new StringDecoder());//把ByteBuf解码成String字符串
                ch.pipeline().addLast("serverHandler",new DelimiterServerHandler());
            }
        });

        try {
            //绑定端口号
            ChannelFuture cf = bootstrap.bind(port).sync();

            System.out.println("DelimiterServer启动完成,端口:"+port);

            //设置同步 阻塞当前线程 直到服务端口关闭
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
          baseGroup.shutdownGracefully();
          workGrop.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new DelimiterServer().startServer(10086);
    }
}
