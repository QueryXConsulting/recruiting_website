package com.queryx.recruiting_website.handler.netty.sevice.impl;

//import com.alibaba.fastjson2.JSON;
//import com.queryx.recruiting_website.common.*;
//import com.queryx.recruiting_website.domain.LoginUser;
//import com.queryx.recruiting_website.domain.TDInterview;
//import com.queryx.recruiting_website.handler.netty.sevice.SimplyHandler;
//import com.queryx.recruiting_website.mapper.InterviewMapper;
//import com.queryx.recruiting_website.mapper.TDUserMapper;
//import com.queryx.recruiting_website.utils.TokenStorage;
//import io.netty.channel.ChannelHandlerContext;
//import jakarta.annotation.Resource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ObjectUtils;
//import org.springframework.util.StringUtils;
//
//import java.util.Map;
//import java.util.Objects;


//@Component
//public class ConnectionMsgHandler implements SimplyHandler {
//    @Resource
//    private InterviewMapper interviewMapper;
//    @Resource
//    private TDUserMapper userMapper;
//    private static final Logger LOG = LoggerFactory.getLogger(ConnectionMsgHandler.class);
//
//    // 连接处理
//    @Override
//    public void handler(ChannelHandlerContext ctx, VideoMsg videoMsg) {
//
//        byte[] body = videoMsg.getBody();
//        if (body == null || body.length == 0) {
//            ctx.close();
//            LOG.error("body错误不应为null");
//            throw new IllegalArgumentException("body error");
//        }
//        ImMsgBody imMsgBody = JSON.parseObject(new String(body), ImMsgBody.class);
//        TDInterview tdInterview = interviewMapper.selectById(imMsgBody.getRoomId());
//        // 房间号所属校验
//        Long imUserId = imMsgBody.getUserId();
//        if (!Objects.equals(tdInterview.getUserId(), imUserId)
//                && !Objects.equals(tdInterview.getCompanyId(),
//                userMapper.selectById(imUserId).getCompanyInfoId())) {
//            ctx.close();
//            LOG.error("所属房间号不对");
//            throw new IllegalArgumentException("roomId error");
//        }
//        String token = imMsgBody.getToken();
//        LoginUser user = (LoginUser) TokenStorage.getUser(token);
//        if ((!StringUtils.hasText(token))) {
//            LOG.error("token校验未通过");
//            ctx.close();
//        }
//
//        // 防止重复请求
//        if (ImContextUtils.getUserId(ctx) != null) {
//            return;
//        }
//
//
//        if (user != null && Objects.equals(imMsgBody.getUserId(), user.getTdUser().getUserId())) {
//            connectionSuccessHandler(ctx, imMsgBody.getUserId(), imMsgBody.getRoomId());
//            return;
//        }
//        ctx.close();
//        LOG.error("检验未通过:{} ", imMsgBody);
//        throw new IllegalArgumentException("检验未通过");
//
//    }
//
//    public void connectionSuccessHandler(ChannelHandlerContext ctx, Long roomId, Long userId) {
//
//        if (ObjectUtils.isEmpty(RoomChannelUtils.get(roomId))) {
//            RoomChannelUtils.put(roomId, Map.of(userId, ctx.channel()));
//        } else {
//            RoomChannelUtils.add(roomId, userId, ctx.channel());
//        }
//
//        ImContextUtils.setUserId(ctx, userId);
//        ImContextUtils.setRoomId(ctx, roomId);
//
//        ImMsgBody imMsgBody = new ImMsgBody();
//        imMsgBody.setRoomId(roomId);
//        imMsgBody.setData("true");
//        imMsgBody.setUserId(userId);
//        VideoMsg videoMsg = VideoMsg.build(ImMsgCode.CONNECTION_MSG_CODE, imMsgBody.getData());
//        LOG.info("[connectionSuccessHandler] 连接成功,userId is {},roomId is {}", userId, roomId);
//        ctx.writeAndFlush(videoMsg);
//    }
//}
