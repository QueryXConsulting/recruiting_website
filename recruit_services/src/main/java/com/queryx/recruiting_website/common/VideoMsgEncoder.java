//package com.queryx.recruiting_website.common;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToByteEncoder;
//
//import java.nio.charset.StandardCharsets;
//
//public class VideoMsgEncoder extends MessageToByteEncoder<VideoMsg> {
//
//
//
//    @Override
//    protected void encode(ChannelHandlerContext ctx, VideoMsg msg, ByteBuf out) throws Exception {
//        String json = serialize(msg);
//        byte[] data = json.getBytes(StandardCharsets.UTF_8);
//
//        // 写入消息长度和消息内容
//        out.writeInt(data.length);
//        out.writeBytes(data);
//    }
//
//    private String serialize(VideoMsg msg) throws Exception {
//        try {
//            //  将 VideoMsg 对象序列化为 JSON 字符串
//            ObjectMapper objectMapper = new ObjectMapper();
//            return objectMapper.writeValueAsString(msg);
//        } catch (Exception e) {
//            throw new RuntimeException("json 转化错误", e);
//        }
//    }
//}
