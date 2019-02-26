package com.wdk.netty.danmu;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.SocketChannel;

/**
 * @Description
 * @Author wangdk
 * @CreateTime 2019-02-26 21:50
 * @Since version 1.0.0
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private String wsUri;

    private static File INDEX;

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

        }
    }
}
