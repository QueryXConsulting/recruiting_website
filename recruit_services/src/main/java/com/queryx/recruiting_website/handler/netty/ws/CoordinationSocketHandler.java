package com.queryx.recruiting_website.handler.netty.ws;


//import com.queryx.recruiting_website.common.ImContextUtils;
//import com.queryx.recruiting_website.handler.netty.sevice.ImHandlerFactory;
//import com.queryx.recruiting_website.handler.netty.sevice.impl.UnConnectHandler;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.handler.codec.http.websocketx.WebSocketFrame;
//import jakarta.annotation.Resource;
//import org.springframework.context.annotation.Configuration;



//@Configuration
//@ChannelHandler.Sharable
//public class CoordinationSocketHandler extends SimpleChannelInboundHandler {
//
//    @Resource
//    private ImHandlerFactory imHandlerFactory;
//    @Resource
//    private UnConnectHandler unConnectHandler;
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//
//        if (msg instanceof WebSocketFrame) {
//            imHandlerFactory.doMsgHandler(ctx, msg);
//        }
//
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        // 正常退出或者断线都会回到这里
//        Long userId = ImContextUtils.getUserId(ctx);
//        Long roomId = ImContextUtils.getRoomId(ctx);
//        if (userId != null && roomId != null) {
//            unConnectHandler.unConnectHandler(ctx,userId,roomId);
//        }
//        ctx.close();
//    }
//
//}
