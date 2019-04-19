package com.wdk.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/2/26 17:04
 * @Since version 1.0.0
 */
public class NettyHttpServer {
    
    /**
     * @Description:
     * 打开Server
     * @Param 
     * @return 
    */
    public void openServer(int port){
        ServerBootstrap bootstrap = new ServerBootstrap(); //启动类

        //设置channel
        bootstrap.channel(NioServerSocketChannel.class);

        //线程组
        EventLoopGroup base = new NioEventLoopGroup(1);//用于监听的线程组
        EventLoopGroup work = new NioEventLoopGroup(8);//用于Io工作的线程组
        bootstrap.group(base,work);

        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            //每次有请求进来 都会执行该方法
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {

                ChannelPipeline pipeline = nioSocketChannel.pipeline();

                //nioSocketChannel.pipeline().addLast() 添加的内容有严格的顺序限制

                //HttpRequestDecoder用于 Http request报文解码
                pipeline.addLast("http-decoder",new HttpRequestDecoder());

                //请求body体很小的时候 聚合 解析的时候 会解析成一个FullHttpRequest对象
                //如果body体很大 不能使用聚合器
                pipeline.addLast("http",new HttpObjectAggregator(65536));

                //HttpResponseEncoder用于 Http response报文编码
                pipeline.addLast("http-response-encoder",new HttpResponseEncoder());

                //设置业务处理Handler
                pipeline.addLast("http-servlet",new HttpServletHandler());

            }
        });

        try {
            ChannelFuture cf = bootstrap.bind(port).sync(); //绑定端口号  设置同步  ChannelFuture channel异步计算的结果.
            System.out.println("服务已经成功启动port:"+port);
            cf.channel().closeFuture().sync();  //底层在 DefaultPromise 里面调用了await()方法 然后调用了Object的 wait()方法,阻塞掉主线程,保证服务端Channel的正常运行
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            base.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    /**
     * @Description:
     * 自定义业务处理Handler
     *
     * netty 接收Http request时候 会解析为两个对象 HttpRequest HttpContent
     * 如果 body体过大 会分为若干个HttpContent 最后一个HttpContent为 LastHttpContent
     * 如果body体不大  可以设置为 合并request请求 这时候会解析为一个对象  包含request信息和body体
     *
    */
    private static class HttpServletHandler extends SimpleChannelInboundHandler{
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
            if(o instanceof LastHttpContent){ //接受完最后一个Body体
                //设置需要返回的response
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");
                response.content().writeBytes("Server is ok".getBytes("UTF-8"));

                //把response写回到管道流  然后关闭管道
                channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    public static void main(String[] args) {
        new NettyHttpServer().openServer(8080);
    }
}


