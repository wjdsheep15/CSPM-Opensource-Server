package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.*;
import com.elastic.cspm.data.dto.ResourceResultResponseDto.ResourceListDto;
import com.elastic.cspm.data.repository.ScanGroupRepository;
import com.elastic.cspm.service.IamService;
import com.elastic.cspm.service.ResourceService;
import com.elastic.cspm.service.ScanGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {
    private final ResourceService resourceService;
    private final IamService iamService;
    private final ScanGroupService scanGroupService;
    private final ScanGroupRepository groupRepository;

    /**
     * IAM 선택 API
     * IAM 레포지토리에서 프론트로 보낼 API가 존재해야 함.
     * 필요한 이유 : IAM에서의 셀렉트 박스는 IAM에 따라 값이 달라지기 때문에.
     */
    @GetMapping("/iam")
    public ResponseEntity<List<IamSelectDto>> getIAMName() {
        List<String> iamNicknames = iamService.getIAMNicknames();
        List<IamSelectDto> iamList = iamNicknames.stream()
                .map(nickname -> {
                    IamSelectDto dto = new IamSelectDto();
                    dto.setNickname(nickname);
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(iamList);
    }


    /**
     * ScanGroup 선택 API
     */
    @GetMapping("/scanGroup")
    public ResponseEntity<List<ScanGroupSelectDto>> getScanGroupName() {
        List<String> group = scanGroupService.getScanGroup();
        List<ScanGroupSelectDto> iamList = group.stream()
                .map(scanGroup -> {
                    ScanGroupSelectDto dto = new ScanGroupSelectDto();
                    dto.setScanGroup(scanGroup);
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(iamList);
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
    @PostMapping("/startScan")
    public ResponseEntity<List<ResourceResultData>> saveDescribe(@RequestBody List<DescribeIamDto> describeIamList) throws Exception {
        log.info("스캔 시작");
        List<ResourceResultData> resourceResultData = resourceService.startDescribe(describeIamList);

        return ResponseEntity.ok(resourceResultData);
    }

    /**
     * 스캔 시작 후 resource와 service 필터
     */
}
