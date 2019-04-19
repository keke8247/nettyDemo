package com.wdk.netty.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * @Description
 * msgpack解码器
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/19 14:44
 * @Since version 1.0.0
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf>{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

        //消息长度
        final int  length = msg.readableBytes();

        byte [] array = new byte[length];

        msg.getBytes(msg.readerIndex(),array,0,length); //把消息转成byte数组

        MessagePack msgpack = new MessagePack();

        out.add(msgpack.read(array)); //通过messagePack把byte数组转成对象 放到解码列表中.
    }
}
