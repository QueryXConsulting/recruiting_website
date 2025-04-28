package com.queryx.recruiting_website.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Author：fjj
 * @Date：2025/3/9 15:47
 */
@Data
public class TDMaterial {

    @TableId(value = "material_id", type = IdType.AUTO)
    @Schema(name = "offers表id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long materialId;

//    @TableField(value = "offers_id")
    @Schema(name = "offer表id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long offerId;

    @Schema(name = "用户id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(name = "公司id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long companyId;

    @Schema(name = "职位id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobId;

    @Schema(name = "身份证", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String identityCard;

    @Schema(name = "体检报告", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String physicalExamination;

    @Schema(name = "学历/学位证书", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String diploma;

    @Schema(name = "证件照", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String identificationPhoto;

    @Schema(name = "银行卡", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankCard;

    @Schema(name = "资格证书", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String qualificationCertificate;

    @Schema(name = "离职证明", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resignCertificate;

    @Schema(name = "其他信息", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String other;

    @Schema(name = "素材状态", description = "0待提交，1通过，2拒绝，3待审核", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(name = "发送时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date sendTime;

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
