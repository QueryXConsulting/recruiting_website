package com.queryx.recruiting_website.handler.netty.sevice.impl;

//import com.alibaba.fastjson2.JSON;
//import com.queryx.recruiting_website.common.*;
//import com.queryx.recruiting_website.handler.netty.sevice.SimplyHandler;
//import io.netty.channel.ChannelHandlerContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;

//@Service
//public class UnConnectHandler implements SimplyHandler {
//    private static final Logger LOG = LoggerFactory.getLogger(UnConnectHandler.class);
//    @Override
//    public void handler(ChannelHandlerContext ctx, VideoMsg videoMsg) {
//        Long userId = ImContextUtils.getUserId(ctx);
//        Long roomId = ImContextUtils.getRoomId(ctx);
//        if (userId == null || roomId == null) {
//            LOG.error("attr 错误,videoMsg: {}", videoMsg);
//            // 可能是错误的消息包，直接放弃连接
//            ctx.close();
//            throw new IllegalArgumentException("attr is error");
//        }
//        // 退出面试间
//        unConnectMsgNotice(ctx,userId,roomId);
//        unConnectHandler(ctx,userId,roomId);
//    }
//
//    private void unConnectMsgNotice(ChannelHandlerContext ctx, Long userId, Long roomId) {
//        ImMsgBody respBody = new ImMsgBody();
//        respBody.setRoomId(roomId);
//        respBody.setUserId(userId);
//        respBody.setData("true");
//        VideoMsg respMsg = VideoMsg.build(ImMsgCode.UNCONNECT_MSG_CODE, JSON.toJSONString(respBody));
//        ctx.writeAndFlush(respMsg);
//        ctx.close();
//    }
//
//    public void unConnectHandler(ChannelHandlerContext ctx, Long userId, Long roomId) {
//        LOG.info("[unConnectHandler] 取消连接成功 ,userId  {},roomId  {}", userId, roomId);
//        RoomChannelUtils.remove(roomId);
//        ImContextUtils.removeUserId(ctx);
//        ImContextUtils.removeRoomId(ctx);
//    }
//
//}
