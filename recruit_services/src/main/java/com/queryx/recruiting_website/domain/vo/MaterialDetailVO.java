package com.queryx.recruiting_website.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class MaterialDetailVO {

    private Long materialId;

    private String identityCard;


    private String physicalExamination;


    private String diploma;


    private String identificationPhoto;


    private String bankCard;


    private String qualificationCertificate;


    private String resignCertificate;

    private String signaturePath;

    private List<String> other;

    private String status;

    private String identityCardStatus;

    private String physicalExaminationStatus;

    private String identificationPhotoStatus;

    private String diplomaStatus;

    private String bankCardStatus;

    private String qualificationCertificateStatus;

    private String resignCertificateStatus;

    private String otherStatus;

    private String signatureStatus;

}
