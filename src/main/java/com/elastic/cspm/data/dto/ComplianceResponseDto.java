package com.elastic.cspm.data.dto;


import com.elastic.cspm.data.entity.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ComplianceResponseDto {

    private LocalDateTime scanTime;

    private String accountId;

    private String accountName;

    private String severity; // 심각도 등급

    private String policyTitle;


    public static ComplianceResponseDto of(ComplianceResult complianceResult, IAM iam) {
        return new ComplianceResponseDto(
                complianceResult.getScanTime(),
                iam.getId().toString(),
                iam.getNickName(),
                complianceResult.getPolicy().getSeverity(),
                complianceResult.getPolicy().getPolicyTitle()
        );
    }

}
