package com.wdk.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;

import java.util.Date;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/29 10:48
 * @Since version 1.0.0
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler{

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //传统的Http接入
        if(msg instanceof FullHttpRequest){
            handleHttpRequest(ctx,(FullHttpRequest)msg);
        }
        //webSocket接入
        else if(msg instanceof WebSocketFrame){
            handleWebSocketFrame(ctx,(WebSocketFrame) msg);
        }
    }

    //处理Http请求
    private void handleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req){
        //Http解码失败 或者 不需要升级为websocket协议的 返回失败
        if(!req.decoderResult().isSuccess() || !"websocket".equals(req.headers().get("Upgrade"))){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //构造握手响应返回  进行协议升级 本机测试
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:8080/websocket",null,false
        );

        handshaker = wsFactory.newHandshaker(req);

        if(null == handshaker){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else{
            handshaker.handshake(ctx.channel(),req);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    //处理WebSocket请求
    private void handleWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame){
        //判断是否是关闭链路指令
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }

        //判断是否是保活ping消息
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PingWebSocketFrame(frame.content().retain()));
            return;
        }

        //本demo仅支持文本消息  不支持二进制消息
        if(!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame types not supported",frame.getClass().getName()));
        }

        //返回应答消息
        String request = ((TextWebSocketFrame) frame).text();

        ctx.channel().write(new TextWebSocketFrame(request+" ,欢迎使用Netty WebSocket服务,现在时刻:"+new Date().toString()));
    }

    //响应客户端请求
    private void sendHttpResponse(ChannelHandlerContext ctx,FullHttpRequest req,FullHttpResponse res){
        if(res.status().code() != 200){ //请求失败
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString().getBytes());

            res.content().writeBytes(buf);

            //释放缓冲区
            buf.release();

            ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
