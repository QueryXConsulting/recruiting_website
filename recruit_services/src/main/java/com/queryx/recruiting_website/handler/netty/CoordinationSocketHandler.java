package com.queryx.recruiting_website.handler.netty;


import com.queryx.recruiting_website.common.ImMsgCode;
import com.queryx.recruiting_website.common.VideoMsg;
import com.queryx.recruiting_website.handler.netty.sevice.SimplyHandler;
import com.queryx.recruiting_website.handler.netty.sevice.impl.ConnectionMsgHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
@ChannelHandler.Sharable
public class CoordinationSocketHandler extends SimpleChannelInboundHandler implements InitializingBean {

    @Resource
    private SimplyHandler simplyHandler;
    private static Map<Integer, SimplyHandler> simplyHandlerMap = new HashMap<>();

    @Resource
    private ApplicationContext applicationContext;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof VideoMsg videoMsg)){
            return;
        }

        simplyHandlerMap.get(videoMsg.getCode());
        if (simplyHandler == null) {
            throw new IllegalArgumentException("msg code is error,code is " + videoMsg.getCode());
        }
        simplyHandler.handler(ctx,videoMsg);
        // TODO 后续 code为视频通话的请求传进来则进入对应的处理程序 判断此时roomId的数量以及面试时间如果通过则准许连接
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 正常退出或者断线都会回到这里 TODO 待修改
        super.channelInactive(ctx);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        simplyHandlerMap.put(ImMsgCode.CONNECTION_MSG_CODE, applicationContext.getBean(ConnectionMsgHandler.class));

    }



}
