package com.elastic.cspm.data.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 필터를 위해 요청을 보낼 RequestDTO
 * IAM 선택, Group 선택 그리고 페이지 선택
 */
@Getter
@Setter
public class ResourceFilterRequestDto {
    private String IAM;
    private String scanGroup;
    int pIndex;
    int pSize;
}
