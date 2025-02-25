package com.queryx.recruiting_website.domain.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class AddInterviewDto {
    private Long companyId;
    private Date interviewDateStart;
    private Date interviewDateEnd;
}
