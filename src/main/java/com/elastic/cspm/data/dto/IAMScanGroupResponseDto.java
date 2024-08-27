package com.elastic.cspm.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IAMScanGroupResponseDto {
    private List<String> iamList;
    private List<String> scanGroupList;
}
