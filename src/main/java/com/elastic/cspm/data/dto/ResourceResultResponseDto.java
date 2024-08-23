package com.elastic.cspm.data.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 보여줄 모든 리소스 리스트 조회
 * 스캔 시간, accountId, resource, resourceId, service??
 */
public class ResourceResultResponseDto {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record ResourceListDto (
            List<ResourceRecordDto> resources,
            int total,
            int totalPage
    ) {}

    @Builder
    public record ResourceRecordDto(
        String scanTime,
        String accountId,
        String resource,
        String resourceId
//        String service 수정
    ){
        public static ResourceRecordDto of(QResourceDto qResourceDto) {
            String formattedScanTime = qResourceDto.getScanTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return ResourceRecordDto.builder()
                    .scanTime(formattedScanTime)
                    .accountId(qResourceDto.getAccountId())
                    .resource(qResourceDto.getResource())
                    .resourceId(qResourceDto.getResourceId())
//                    .service(qResourceDto.getService())
                    .build();
        }
    }
}
