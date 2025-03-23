package com.queryx.recruiting_website.common;

import lombok.Data;

import java.util.Date;

@Data
public class ImMsgBody {
    private Long roomId;
    private Long userId;
    private int code;
    private String data;
    private Date InterviewDate;
    private String token;
}
