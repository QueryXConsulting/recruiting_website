package com.queryx.recruiting_website.vo;
import lombok.Data;
import java.util.Date;


@Data
public class JobCompanyListVo {
    /**
     * 岗位id
     */
    private Long jobId;

    /**
     * 工作职位名称
     */
    private String jobPosition;

    /**
     * 教育程度
     */
    private String jobEducation;
    /**
     * 工作经验
     */
    private String jobExperience;

    /**
     * 岗位创建时间
     */
    private Date jobTime;

    /**
     * 工作性质
     */
    private String jobNature;
    /**
     * 0代表待审核,1代表审核通过
     */
    private String jobReview;
    /**
     * 0关闭,1代表发布
     */
    private String jobStatus;


}
