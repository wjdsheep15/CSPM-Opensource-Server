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
    LocalDateTime scanTime;
    String resourceId;
    String scanTarget;
    Boolean isAllSuccess;
    List<DescribeResult> describeEntityList;

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
