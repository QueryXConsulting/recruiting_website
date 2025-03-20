package com.queryx.recruiting_website.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class ImContextUtils {

    public static void setUserId(ChannelHandlerContext ctx,Long userId){
        AttributeKey<Long> myAttributeKey = AttributeKey.valueOf("userId");
        ctx.attr(myAttributeKey).set(userId);
    }

    public static void removeUserId(ChannelHandlerContext ctx){
        AttributeKey<Long> myAttributeKey = AttributeKey.valueOf("userId");
        ctx.attr(myAttributeKey).remove();
    }

    public static Long getUserId(ChannelHandlerContext ctx){
        AttributeKey<Long> myAttributeKey = AttributeKey.valueOf("userId");
        return ctx.attr(myAttributeKey).get();
    }

    public static Long getRoomId(ChannelHandlerContext ctx) {
        AttributeKey<Long> myAttributeKey = AttributeKey.valueOf("roomId");
        return ctx.attr(myAttributeKey).get();
    }

    public static void setRoomId(ChannelHandlerContext ctx, Long roomId) {
        AttributeKey<Long> myAttributeKey = AttributeKey.valueOf("roomId");
        ctx.attr(myAttributeKey).set(roomId);
    }

}
