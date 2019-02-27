package com.wdk.netty.danmu;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @Description
 * @Author wangdk
 * @CreateTime 2019-02-26 21:50
 * @Since version 1.0.0
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private String wsUri;

    private static File INDEX;

    static {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = location.toURI() + "WebSocketDanMu.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate WebsocketChatClient.html", e);
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (wsUri.equalsIgnoreCase(request.uri())){
            ctx.fireChannelRead(request.retain());
        }else{
            RandomAccessFile file = new RandomAccessFile(INDEX,"r");
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(),HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");
            boolean keepAlive = HttpUtil.isKeepAlive(request);

            if(keepAlive){
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH,file.length());
                response.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
            }

            ctx.write(response);
            ctx.write(new DefaultFileRegion(file.getChannel(),0,file.length()));

            ChannelFuture cf = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            if(!keepAlive){
                cf.addListener(ChannelFutureListener.CLOSE);
            }

            file.close();
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
