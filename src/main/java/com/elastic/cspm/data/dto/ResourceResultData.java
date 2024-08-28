package com.elastic.cspm.data.dto;

import com.elastic.cspm.data.entity.DescribeResult;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ResourceResultData {
    private LocalDateTime scanTime;
    private String resourceId;
    private String scanTarget;
    private Boolean isAllSuccess;
    private List<DescribeResult> describeEntityList; // 스캔 결과 리스트

    public static ResourceResultData of(LocalDateTime scanTime, String resourceId, String scanTarget, Boolean isSuccess, List<DescribeResult> describeEntityList) {
        return ResourceResultData.builder()
                .scanTime(scanTime)
                .resourceId(resourceId)
                .scanTarget(scanTarget)
                .isAllSuccess(isSuccess)
                .describeEntityList(describeEntityList)
                .build();
    }
}
