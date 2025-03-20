package com.queryx.recruiting_website.handler.netty.ws;

import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.LoginUser;
import com.queryx.recruiting_website.handler.netty.sevice.impl.ConnectionMsgHandler;
import com.queryx.recruiting_website.utils.TokenStorage;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
@ChannelHandler.Sharable
public class WsSharkHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(WsSharkHandler.class);

    @Resource
    private ConnectionMsgHandler connectionMsgHandler;
    private WebSocketServerHandshaker webSocketServerHandshaker;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //握手接入ws
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, ((FullHttpRequest) msg));
            return;
        }

        //正常关闭链路
        if (msg instanceof CloseWebSocketFrame) {
            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) ((WebSocketFrame) msg).retain());
            return;
        }
        //将消息传递给下一个链路处理器去处理
        ctx.fireChannelRead(msg);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        // ws://127.0.0.1:8080/{token}/{roomId}/{userId}
        String webSocketUrl = "ws://" + Common.ip + ":" + Common.port;
        // 构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(webSocketUrl, null, false);
        String uri = msg.uri();
        String[] paramArr = uri.split("/");
        String token = paramArr[1];
        LoginUser user = (LoginUser) TokenStorage.getUser(token);
        if (ObjectUtils.isEmpty(user)) {
            LOG.error("[WsSharkHandler] token 校验不通过！");
            ctx.close();
            return;
        }
        //建立ws的握手连接
        webSocketServerHandshaker = wsFactory.newHandshaker(msg);
        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        }

        ChannelFuture channelFuture = webSocketServerHandshaker.handshake(ctx.channel(), msg);
        //首次握手建立ws连接后，返回一定的内容给到客户端
        if (channelFuture.isSuccess()) {
            Long roomId = Long.valueOf(paramArr[2]);
            Long userId = Long.valueOf(paramArr[3]);
            connectionMsgHandler.connectionSuccessHandler(ctx,roomId,userId);
            LOG.info("[WebsocketSharkHandler] 连接成功");
        }
    }


}
