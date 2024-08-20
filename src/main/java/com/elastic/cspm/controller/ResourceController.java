package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.DescribeIamDto;
import com.elastic.cspm.data.dto.ResourceFilterRequestDto;
import com.elastic.cspm.data.dto.ResourceResultResponseDto.*;
import com.elastic.cspm.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {
    private final ResourceService resourceService;

    /**
     * IAM 선택과 scanGroup을 필터링하여 조회.
     */
    @GetMapping("/list")
    public ResponseEntity<ResourceListDto> getResources(ResourceFilterRequestDto resourceFilterDto) throws Exception {
        log.info("getResources: IAM = {}, scanGroup = {}",
                resourceFilterDto.getIAM(), resourceFilterDto.getScanGroup());


        ResourceListDto allResources = resourceService.getAllResources(resourceFilterDto);
        return ResponseEntity.ok(allResources);
    }

    /**
     * 스캔 시작 API
     */
    /*@PostMapping
    public ResponseEntity<Void> saveDescribe(@RequestBody List<DescribeIamDto> describeIamList) throws Exception {
        return resourceService.startDescribe(describeIamList);
    }*/

    /**
     * 스캔 시작 후 resource와 service 필터
     */
}
