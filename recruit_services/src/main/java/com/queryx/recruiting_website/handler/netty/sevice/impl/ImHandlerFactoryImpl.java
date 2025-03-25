package com.queryx.recruiting_website.handler.netty.sevice.impl;

//import com.queryx.recruiting_website.common.ImMsgCode;
//import com.queryx.recruiting_website.common.VideoMsg;
//import com.queryx.recruiting_website.handler.netty.sevice.ImHandlerFactory;
//import com.queryx.recruiting_website.handler.netty.sevice.SimplyHandler;
//import io.netty.channel.ChannelHandlerContext;
//import jakarta.annotation.Resource;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;

//@Component
//public class ImHandlerFactoryImpl implements ImHandlerFactory, InitializingBean {
//    private static  Map<Integer, SimplyHandler> simplyHandlerMap = new HashMap<>();
//    @Resource
//    private ApplicationContext applicationContext;
//
//    @Override
//    public void doMsgHandler(ChannelHandlerContext ctx, Object msg) {
//        if (!(msg instanceof VideoMsg videoMsg)){
//            return;
//        }
//
//        SimplyHandler simplyHandler = simplyHandlerMap.get(videoMsg.getCode());
//        if (simplyHandler == null) {
//            throw new IllegalArgumentException("msg code is error,code is " + videoMsg.getCode());
//        }
//        simplyHandler.handler(ctx,videoMsg);
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        simplyHandlerMap.put(ImMsgCode.UNCONNECT_MSG_CODE,applicationContext.getBean(UnConnectHandler.class));
//        simplyHandlerMap.put(ImMsgCode.CONNECTION_MSG_CODE, applicationContext.getBean(ConnectionMsgHandler.class));
//        simplyHandlerMap.put(ImMsgCode.VIDEO_MSG_CODE,applicationContext.getBean(JoinVideoHandler.class));
//    }
//}
