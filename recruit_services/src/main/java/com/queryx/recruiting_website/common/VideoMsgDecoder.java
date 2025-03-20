package com.queryx.recruiting_website.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class VideoMsgDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 12) {
            return;
        }

        in.markReaderIndex();

        // 读取消息长度
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        // 读取消息内容
        byte[] data = new byte[length];
        in.readBytes(data);

        VideoMsg videoMsg = deserialize(data);
        out.add(videoMsg);
    }

    private VideoMsg deserialize(byte[] data) throws Exception {
        // 反序列化
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (VideoMsg) ois.readObject();
        }
    }
}
