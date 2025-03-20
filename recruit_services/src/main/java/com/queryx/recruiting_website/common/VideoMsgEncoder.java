package com.queryx.recruiting_website.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class VideoMsgEncoder extends MessageToByteEncoder<VideoMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, VideoMsg msg, ByteBuf out) throws Exception {
        // 序列化 VideoMsg 对象
        byte[] data = serialize(msg);

        // 写入消息长度和消息内容
        out.writeInt(data.length);
        out.writeBytes(data);
    }

    private byte[] serialize(VideoMsg msg) throws Exception {
        // 使用 Java 的序列化机制进行序列化
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(msg);
            oos.flush();
            return baos.toByteArray();
        }
    }
}
