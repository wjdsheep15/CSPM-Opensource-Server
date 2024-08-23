package com.elastic.cspm.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 프로젝션 : 엔티티 전체의 값이 아닌 조회 대상을 지정해 원하는 값만 조회.
 * DB 쿼리에서 특정 필드나 데이터를 선택하여 반환하는 과정.
 * 전체 테이블의 데이터를 조회하는 대신, 필요한 일부 필드만 선택하여 결과를 반환함으로써 데이터 전송량을 줄이고 성능 향상.
 */
@Getter
@Setter
@AllArgsConstructor
public class QResourceDto {
    private LocalDateTime scanTime;
    private String accountId;
    private String resource;
    private String resourceId;
//    private String service;
}
