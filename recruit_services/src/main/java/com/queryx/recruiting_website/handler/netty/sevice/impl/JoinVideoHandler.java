package com.queryx.recruiting_website.handler.netty.sevice.impl;

//import com.alibaba.fastjson2.JSON;
//import com.queryx.recruiting_website.common.*;
//import com.queryx.recruiting_website.handler.netty.sevice.SimplyHandler;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandlerContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.Map;


//@Service
//public class JoinVideoHandler implements SimplyHandler {
//    private static final Logger LOG = LoggerFactory.getLogger(JoinVideoHandler.class);
//
//
//    @Override
//    public void handler(ChannelHandlerContext ctx, VideoMsg videoMsg) {
//
//        byte[] body = videoMsg.getBody();
//        if (body == null || body.length == 0) {
//            ctx.close();
//            LOG.error("[JoinVideoHandler] body错误不应为null");
//            return;
//        }
//        ImMsgBody imMsgBody = JSON.parseObject(new String(body), ImMsgBody.class);
//        // 时间校验
//        Date currentTime = new Date();
//        if (currentTime.before(imMsgBody.getInterviewDate())) {
//            LOG.info("未到面试时间");
//            return;
//        }
//
//        // 交换sdp信息
//        handleSdpInfo(ctx, imMsgBody);
//
//    }
//
//    private void handleSdpInfo(ChannelHandlerContext ctx, ImMsgBody imMsgBody) {
//        Long roomId = ImContextUtils.getRoomId(ctx);
//        Map<Long, Channel> channelMap = RoomChannelUtils.get(roomId);
//        Long currentUserId = ImContextUtils.getUserId(ctx);
//
//        // 检查 channelMap 是否有效
//        if (channelMap == null || channelMap.isEmpty()) {
//            LOG.error("没有 channel , roomId: {}", roomId);
//            return;
//        }
//
//        // 获取另一个用户的 Channel
//        Channel targetChannel = channelMap.entrySet().stream()
//                .filter(entry -> !entry.getKey().equals(currentUserId))
//                .map(Map.Entry::getValue)
//                .findFirst()
//                .orElse(null);
//
//        // 检查目标 Channel 是否在线，以及发送 SDP 或 ICE信息
//        if (targetChannel != null && targetChannel.isActive()) {
//            int code = imMsgBody.getCode();
//            VideoMsg responseMsg = VideoMsg.build(code, imMsgBody.getData());
//            targetChannel.writeAndFlush(responseMsg);
//        }
//    }
//
//}
