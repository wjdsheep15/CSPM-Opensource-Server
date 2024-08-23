package com.elastic.cspm.data.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 필터를 위해 요청을 보낼 RequestDTO
 * IAM 선택, Group 선택 그리고 페이지 선택
 * pageIndex : 현재 요청된 페이지의 인덱스 -> 인덱스는 0부터 시작하므로, 첫 번째 페이지를 요청할 때는 0
 * pageSize : 한 페이지에 포함될 항목의 수를 나타냄. 한 페이지에 몇 개의 데이터 항목을 표시할지 결정.
 */
@Getter
@Setter
public class ResourceFilterRequestDto {
    private String iam;
    private String scanGroup;
    private int pageIndex;
    private int pageSize;

    private ResourceFilterRequestDto(String iam, String scanGroup, int pageIndex, int pageSize) {
        this.iam = iam;
        this.scanGroup = scanGroup;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }
}
