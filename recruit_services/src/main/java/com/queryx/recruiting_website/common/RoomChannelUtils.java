//package com.queryx.recruiting_website.common;
//
//import io.netty.channel.Channel;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//
//public class RoomChannelUtils {
//    private static final Map<Long, Map<Long, Channel>> roomChannels = new ConcurrentHashMap<>();
//
//    public static Map<Long, Channel> get(Long roomId) {
//        return roomChannels.get(roomId);
//    }
//
//    public static void put(Long roomId, Map<Long, Channel> userMap) {
//        roomChannels.put(roomId, userMap);
//    }
//
//    public static void add(Long roomId, Long userId, Channel ctx) {
//        Map<Long, Channel> userMaps = roomChannels.get(roomId);
//        if (userMaps.size() < 3) {
//            userMaps.put(userId, ctx);
//        }
//    }
//
//    public static Boolean check(Long roomId) {
//        Map<Long, Channel> userMaps = roomChannels.get(roomId);
//        return userMaps.size() < 3;
//    }
//
//    public static void remove(Long roomId) {
//        roomChannels.remove(roomId);
//    }
//
//}
