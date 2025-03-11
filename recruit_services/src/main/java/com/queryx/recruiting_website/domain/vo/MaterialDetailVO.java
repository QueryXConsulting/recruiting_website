package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class MaterialDetailVO {


    private String identityCard;


    private String physicalExamination;


    private String diploma;


    private String identificationPhoto;


    private String bankCard;


    private String qualificationCertificate;


    private String resignCertificate;


    private List<String> other;

}
