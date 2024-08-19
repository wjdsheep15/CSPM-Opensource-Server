package com.elastic.cspm.data.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 프로젝션 : 엔티티 전체의 값이 아닌 조회 대상을 지정해 원하는 값만 조회.
 */
@Getter
@Setter
@NoArgsConstructor
public class QResourceDto {
    private LocalDateTime scanTime;
    private String accountId;
    private String resourceId;
    private String resource;
    private String service;
}
