package com.queryx.recruiting_website.domain;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class TDMaterial {


    @TableId
    private Long materialId;


    private Long offerId;


    private Long userId;


    private Long companyId;


    private Long jobId;





    private String identityCard;


    private String physicalExamination;


    private String diploma;


    private String identificationPhoto;


    private String bankCard;


    private String qualificationCertificate;


    private String resignCertificate;


    private String other;


    private String status;

    private Date sendTime;
}
