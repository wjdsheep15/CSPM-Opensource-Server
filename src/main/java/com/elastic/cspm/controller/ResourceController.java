package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.DescribeIamDto;
import com.elastic.cspm.data.dto.ResourceFilterRequestDto;
import com.elastic.cspm.data.dto.ResourceResultResponseDto.*;
import com.elastic.cspm.data.repository.IamRepository;
import com.elastic.cspm.service.IamService;
import com.elastic.cspm.service.ResourceService;
import com.elastic.cspm.service.ScanGroupService;
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
    private final IamService iamService;
    private final ScanGroupService scanGroupService;

    /**
     * IAM 선택 API
     */
    @GetMapping("/iam")
    public ResponseEntity<List<String>> getIAMName() {
        List<String> iamNicknames = iamService.getIAMNicknames();
        return ResponseEntity.ok(iamNicknames);
    }

    /**
     * ScanGroup 선택 API
     */
//    @GetMapping("/scanGroup")
//    public ResponseEntity<List<String>> getScanGroup() {
//        List<String> iamNicknames = iamService.getIAMNicknames();
//        return ResponseEntity.ok(iamNicknames);
//    }

    /**
     * IAM 선택과 scanGroup을 필터링하여 조회.
     */
    @GetMapping("/list")
    public ResponseEntity<ResourceListDto> getResources(@RequestBody ResourceFilterRequestDto resourceFilterDto) throws Exception {
        log.info(
                resourceFilterDto.getIam() + resourceFilterDto.getScanGroup());


        ResourceListDto allResources = resourceService.getAllResources(resourceFilterDto);
        return ResponseEntity.ok(allResources);
    }

    /**
     * 스캔 시작 API
     */
    @PostMapping
    public ResponseEntity<Void> saveDescribe(@RequestBody List<DescribeIamDto> describeIamList) throws Exception {
        return resourceService.startDescribe(describeIamList);
    }

    /**
     * 스캔 시작 후 resource와 service 필터
     */
}
