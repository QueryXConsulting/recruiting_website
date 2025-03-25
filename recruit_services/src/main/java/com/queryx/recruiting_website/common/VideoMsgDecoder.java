//package com.queryx.recruiting_website.common;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.queryx.recruiting_website.netty.NettySever;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.ByteToMessageDecoder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//
//public class VideoMsgDecoder extends ByteToMessageDecoder {
//    private static final Logger LOG = LoggerFactory.getLogger(NettySever.class);
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        if (in.readableBytes() < 4) {
//            return;
//        }
//
//        in.markReaderIndex();
//
//        // 读取消息长度
//        int length = in.readInt();
//        if (in.readableBytes() < length) {
//            in.resetReaderIndex();
//            return;
//        }
//
//        // 读取消息内容
//        byte[] data = new byte[length];
//        in.readBytes(data);
//
//        try {
//            VideoMsg videoMsg = deserialize(data);
//            out.add(videoMsg);
//        } catch (Exception e) {
//            LOG.error("json 错误", e);
//        }
//    }
//
//    private VideoMsg deserialize(byte[] data) throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        return objectMapper.readValue(data, VideoMsg.class);
//    }
//}
