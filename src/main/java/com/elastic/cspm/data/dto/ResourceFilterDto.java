package com.elastic.cspm.data.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 필터를 위해 요청을 보낼 DTO
 * IAM 선택, Group 선택, 리소스, 서비스 그리고 페이지 선택
 */
@Getter
@Setter
public class ResourceFilterDto {
    private String IAM;
    private String scanGroup;
    private String resource;
    private String service;
    int pIndex;
    int pSize;
}
