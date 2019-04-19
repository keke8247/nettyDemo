package com.wdk.netty.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * @Description
 * msgpack编码器
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/19 14:39
 * @Since version 1.0.0
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object>{
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

        MessagePack msgpack = new MessagePack();

        byte [] raw = msgpack.write(msg); //通过messagePack

        out.writeBytes(raw); //把byte数组写入到 ByteBuf
    }
}
