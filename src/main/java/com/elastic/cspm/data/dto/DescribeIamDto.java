package com.elastic.cspm.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 스캔 시작할 시 전달할 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class DescribeIamDto {
    @Schema(description = "IAM 선택", nullable = false)
    private String iam;

    @Schema(description = "Group 선택", nullable = false)
    private String scanGroup;
}
