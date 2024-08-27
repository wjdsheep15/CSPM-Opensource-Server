package com.elastic.cspm.data.dto;


import com.elastic.cspm.data.entity.ComplianceResult;
import com.elastic.cspm.data.entity.Member;
import com.elastic.cspm.data.entity.Policy;
import com.elastic.cspm.data.entity.ScanGroup;
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


    public static ComplianceResponseDto of(ComplianceResult complianceResult, Member member, Policy policy) {
        return new ComplianceResponseDto(
                complianceResult.getScanTime(),
                member.getAccountId(),
                member.getIamName(),
                policy.getSeverity(),
                policy.getPolicyTitle()
        );
    }

}
