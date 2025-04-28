package com.queryx.recruiting_website.domain.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MaterialDetailStatusVO {
    @Schema(name = "身份证状态", description = "0未提交，1通过，2打回，3待审核", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String identityCardStatus;

    @Schema(name = "体检状态", description = "0未提交，1通过，2打回，3待审核", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String physicalExaminationStatus;

    @Schema(name = "认证照片状态", description = "0未提交，1通过，2打回，3待审核", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String identificationPhotoStatus;

    @Schema(name = "学历证书状态", description = "0未提交，1通过，2打回，3待审核", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String diplomaStatus;

    @Schema(name = "银行卡状态", description = "0未提交，1通过，2打回，3待审核", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankCardStatus;

    @Schema(name = "资格证书状态", description = "0未提交，1通过，2打回，3待审核", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String qualificationCertificateStatus;

    @Schema(name = "离职证明状态", description = "0未提交，1通过，2打回，3待审核", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resignCertificateStatus;

    @Schema(name = "其他状态", description = "0未提交，1通过，2打回，3待审核", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String otherStatus;

    @Schema(name = "offer电子签名", description = "0未提交，1通过，2打回，3待审核", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String signatureStatus;
}
