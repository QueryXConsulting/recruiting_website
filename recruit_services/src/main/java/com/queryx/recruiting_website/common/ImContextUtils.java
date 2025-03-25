//package com.queryx.recruiting_website.common;
//
//import io.netty.channel.ChannelHandlerContext;
//
//
//public class ImContextUtils {
//
//    public static void setUserId(ChannelHandlerContext ctx,Long userId){
//        ctx.attr(ImContextAttr.USER_ID).set(userId);
//    }
//
//    public static void removeUserId(ChannelHandlerContext ctx){
//        ctx.attr(ImContextAttr.USER_ID).remove();
//    }
//
//    public static Long getUserId(ChannelHandlerContext ctx){
//        return ctx.attr(ImContextAttr.USER_ID).get();
//    }
//
//    public static Long getRoomId(ChannelHandlerContext ctx) {
//        return ctx.attr(ImContextAttr.ROOM_ID).get();
//    }
//
//    public static void setRoomId(ChannelHandlerContext ctx, Long roomId) {
//        ctx.attr(ImContextAttr.ROOM_ID).set(roomId);
//    }
//
//    public static void removeRoomId(ChannelHandlerContext ctx){
//        ctx.attr(ImContextAttr.ROOM_ID).remove();
//    }
//
//}
