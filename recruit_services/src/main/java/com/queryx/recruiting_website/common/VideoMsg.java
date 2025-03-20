package com.queryx.recruiting_website.common;

import lombok.Data;

import java.util.Date;

@Data
public class VideoMsg {
    // 用于标记当前消息的作用
    private int code;
    // 存储消息体的内容 按字节的方式去存放
    private byte[] body;

    public static VideoMsg build(int code,String data){
        VideoMsg videoMsg = new VideoMsg();
        videoMsg.setCode(code);
        videoMsg.setBody(data.getBytes());
        return videoMsg;
    }
}
