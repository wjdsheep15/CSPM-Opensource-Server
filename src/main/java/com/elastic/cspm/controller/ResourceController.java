package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.DescribeIamDto;
import com.elastic.cspm.data.dto.ResourceFilterRequestDto;
import com.elastic.cspm.data.dto.ResourceResultResponseDto.ResourceListDto;
import com.elastic.cspm.data.dto.IamSelectDto;
import com.elastic.cspm.data.dto.ScanGroupSelectDto;
import com.elastic.cspm.data.repository.GroupRepository;
import com.elastic.cspm.service.IamService;
import com.elastic.cspm.service.ResourceService;
import com.elastic.cspm.service.ScanGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {
    private final ResourceService resourceService;
    private final IamService iamService;
    private final ScanGroupService scanGroupService;
    private final GroupRepository groupRepository;

    /**
     * IAM 선택 API
     * IAM 레포지토리에서 프론트로 보낼 API가 존재해야 함.
     * 필요한 이유 : IAM에서의 셀렉트 박스는 IAM에 따라 값이 달라지기 때문에.
     */
    @GetMapping("/iam-and-scanGroup")
    public ResponseEntity<Map<String, List<?>>> getIAMAndScanGroupNames() {
        // IAM 이름
        List<String> iamNicknames = iamService.getIAMNicknames();
        List<IamSelectDto> iamList = iamNicknames.stream()
                .map(nickname -> {
                    IamSelectDto dto = new IamSelectDto();
                    dto.setNickname(nickname);
                    return dto;
                })
                .collect(Collectors.toList());

        // ScanGroup 이름
        List<String> group = scanGroupService.getScanGroup();
        List<ScanGroupSelectDto> scanGroupList = group.stream()
                .map(scanGroup -> {
                    ScanGroupSelectDto dto = new ScanGroupSelectDto();
                    dto.setScanGroup(scanGroup);
                    return dto;
                })
                .collect(Collectors.toList());

        Map<String, List<?>> response = new HashMap<>();
        response.put("iamList", iamList);
        response.put("scanGroupList", scanGroupList);

        return ResponseEntity.ok(response);
    }


    /**
     * IAM 선택과 scanGroup을 필터링하여 조회.
     */
    @GetMapping("/list")
    public ResponseEntity<ResourceListDto> getResources(@RequestBody ResourceFilterRequestDto resourceFilterDto) throws Exception {
        log.info("IAM : {}, GroupScan : {}", resourceFilterDto.getIam(), resourceFilterDto.getScanGroup());
        log.info("pIndex : {}, pSize : {}", resourceFilterDto.getPageIndex(), resourceFilterDto.getPageSize());

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
