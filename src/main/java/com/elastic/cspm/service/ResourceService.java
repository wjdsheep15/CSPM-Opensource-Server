package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.ResourceFilterDto;
import com.elastic.cspm.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.elastic.cspm.data.dto.ResourceResultDto.ResourceListDto;
import static com.elastic.cspm.data.dto.ResourceResultDto.ResourceRecordDto;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    // 스캔시간, AccountId, 리소스, 리소스ID, 서비스
    public ResourceListDto getAllResources(ResourceFilterDto resourceFilterDto) throws Exception {
        // DescriptionResult 정보 리스트 반환
        try {
            // 페이징
            Pageable pageable = PageRequest.of(resourceFilterDto.getPIndex(), resourceFilterDto.getPSize());

            Page<ResourceRecordDto> resources = resourceRepository.findResourceList(
                    pageable,
                    resourceFilterDto
            ).map(ResourceRecordDto::of);

            return new ResourceListDto(
                    resources.getContent(),
                    (int) resources.getTotalElements(),
                    resources.getTotalPages()
            );
        } catch (Exception e) {
            log.debug(ExceptionUtils.getStackTrace(e));
            throw new Exception(String.valueOf(NOT_FOUND));
        }
    }
}

