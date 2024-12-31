package com.queryx.recruiting_website.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * (TDInterviewDate)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
public class TDInterviewDate {



    /**
     * 面试时间id
     */
    @TableId(value = "interview_date_id", type = IdType.AUTO)
    private Long interviewDateId;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 面试时间
     */
    private Date interviewDate;
    /**
     * 0代表未删除,1代表已删除
     */
    private String isDeleted;

}

