package com.queryx.recruiting_website.handler.netty.sevice;

import com.queryx.recruiting_website.common.VideoMsg;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

public interface SimplyHandler {

    void handler(ChannelHandlerContext channelHandlerContext, VideoMsg videoMsg );
}
