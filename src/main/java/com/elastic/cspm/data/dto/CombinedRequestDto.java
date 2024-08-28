package com.elastic.cspm.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CombinedRequestDto {
    private List<DescribeIamDto> describeIamList;
    private ResourceFilterRequestDto resourceFilterDto;
}
