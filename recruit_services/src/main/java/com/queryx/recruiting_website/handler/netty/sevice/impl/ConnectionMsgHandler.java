package com.queryx.recruiting_website.handler.netty.sevice.impl;

import com.alibaba.fastjson2.JSON;
import com.queryx.recruiting_website.common.ImContextUtils;
import com.queryx.recruiting_website.common.ImMsgBody;
import com.queryx.recruiting_website.common.ImMsgCode;
import com.queryx.recruiting_website.common.VideoMsg;
import com.queryx.recruiting_website.domain.LoginUser;
import com.queryx.recruiting_website.handler.netty.sevice.SimplyHandler;
import com.queryx.recruiting_website.utils.TokenStorage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionMsgHandler implements SimplyHandler {
    private Map<Long, Channel> roomChannels = new ConcurrentHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionMsgHandler.class);

    // 连接处理
    @Override
    public void handler(ChannelHandlerContext ctx, VideoMsg videoMsg) {
        //防止重复请求
        if (ImContextUtils.getUserId(ctx) != null) {
            return;
        }
        byte[] body = videoMsg.getBody();
        if (body == null || body.length == 0) {
            ctx.close();
            LOG.error("body错误不应为null");
            throw new IllegalArgumentException("body error");
        }
        ImMsgBody imMsgBody = JSON.parseObject(new String(body), ImMsgBody.class);
        String token = imMsgBody.getToken();
        LoginUser user = (LoginUser) TokenStorage.getUser(token);
        if ((!StringUtils.hasText(token)) || ObjectUtils.isEmpty(user)) {
            LOG.error("token校验未通过");
            ctx.close();
        }

        if (Objects.equals(imMsgBody.getUserId(), user.getTdUser().getUserId())) {
            connectionSuccessHandler(ctx, imMsgBody.getUserId(), imMsgBody.getRoomId());
            return;
        }
        ctx.close();
        LOG.error("检验未通过:{} ", imMsgBody);
        throw new IllegalArgumentException("检验未通过");

    }

    public void connectionSuccessHandler(ChannelHandlerContext ctx, Long roomId, Long userId) {
        if (ObjectUtils.isEmpty(roomChannels.get(roomId))) {
            roomChannels.put(roomId, ctx.channel());
        }
        ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setRoomId(roomId);
        imMsgBody.setData("true");
        imMsgBody.setUserId(userId);
        VideoMsg videoMsg = VideoMsg.build(ImMsgCode.CONNECTION_MSG_CODE, imMsgBody.getData());
        ImContextUtils.setUserId(ctx, userId);
        ImContextUtils.setRoomId(ctx, roomId);
        LOG.info("[connectionSuccessHandler] 连接成功,userId is {},roomId is {}", userId, roomId);
        ctx.writeAndFlush(videoMsg);
    }
}
