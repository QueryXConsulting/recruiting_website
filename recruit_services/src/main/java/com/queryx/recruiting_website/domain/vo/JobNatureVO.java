package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

/**
 * @Author：fjj
 * @Date：2025/3/26 11:08
 */
@Data
@Tag(name = "职位性质", description = "职位性质的实体类")
public class JobNatureVO {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "性质id", implementation = Long.class)
    private Long natureId;

    @Schema(name = "性质名称", example = "全职", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String natureName;
}
