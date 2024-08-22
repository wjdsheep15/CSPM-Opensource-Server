package com.elastic.cspm.data.dto;

import com.elastic.cspm.data.entity.DescribeResult;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ResourceResultData {
//    String iamNickName;
//    String scanGroup;
    Boolean isAllSuccess;
    List<DescribeResult> describeEntityList;

    public static ResourceResultData of(Boolean isSuccess, List<DescribeResult> describeEntityList) {
        return ResourceResultData.builder()
//                .iamNickName(iamNickName)
//                .scanGroup(scanGroup)
                .isAllSuccess(isSuccess)
                .describeEntityList(describeEntityList)
                .build();
    }
}
