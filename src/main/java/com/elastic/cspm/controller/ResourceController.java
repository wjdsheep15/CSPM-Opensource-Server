package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.*;
import com.elastic.cspm.data.repository.ScanGroupRepository;
import com.elastic.cspm.service.IamService;
import com.elastic.cspm.service.ResourceService;
import com.elastic.cspm.service.ScanGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.elastic.cspm.data.dto.ResourceResultResponseDto.ResourceListDto;

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
    private final ScanGroupRepository groupRepository;

    /**
     * IAM 선택 API
     * IAM 레포지토리에서 프론트로 보낼 API가 존재해야 함.
     * 필요한 이유 : IAM에서의 셀렉트 박스는 IAM에 따라 값이 달라지기 때문에.
     */
//    @GetMapping("/iam")
//    public ResponseEntity<List<IamSelectDto>> getIAMName() {
//        List<String> iamNicknames = iamService.getIAMNicknames();
//        List<IamSelectDto> iamList = iamNicknames.stream()
//                .map(nickname -> {
//                    IamSelectDto dto = new IamSelectDto();
//                    dto.setNickname(nickname);
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(iamList);
//    }

    /**
     * ScanGroup 선택 API
     */
//    @GetMapping("/scanGroup")
//    public ResponseEntity<List<ScanGroupSelectDto>> getScanGroupName() {
//        List<String> group = scanGroupService.getScanGroup();
//        List<ScanGroupSelectDto> iamList = group.stream()
//                .map(scanGroup -> {
//                    ScanGroupSelectDto dto = new ScanGroupSelectDto();
//                    dto.setScanGroup(scanGroup);
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//        log.info("group : {}", group);
//        log.info("scanGroup : {}", iamList);
//
//        return ResponseEntity.ok(iamList);
//    }

    /**
     * IAM 선택과 ScanGroup을 같은 API에.
     * 이렇게 한다면 IamSelectDto, ScanGroupSelectDto 삭제.
     */
    @GetMapping("/iam-scanGroup")
    public ResponseEntity<IAMScanGroupResponseDto> getIAMAndScanGroupNames() {
        // IAM Nicknames 가져오기
        List<String> iamNicknames = iamService.getIAMNicknames();
        log.info("iamNicknames: {}", iamNicknames);

        // ScanGroup Names 가져오기
        List<String> scanGroups = scanGroupService.getScanGroup();
        log.info("scanGroups: {}", scanGroups);


        // 두 리스트를 하나의 DTO에 담기
        IAMScanGroupResponseDto responseDto = new IAMScanGroupResponseDto();
        responseDto.setIamList(iamNicknames);
        responseDto.setScanGroupList(scanGroups);

        return ResponseEntity.ok(responseDto);
    }


    /**
     * 스캔 시작 로직 진행 후 IAM 선택과 scanGroup을 필터링하여 조회.
     */
    @PostMapping("/start-scan-list")
    public ResponseEntity<Map<String, Object>> getResourcesAndStartScan(@RequestBody CombinedRequestDto combinedRequestDto) throws Exception {
        log.info("스캔 시작");
        // iam과 group 선택하지 않으면 에러가 발생.
        List<ResourceResultData> resourceResultData = resourceService.startDescribe(combinedRequestDto.getDescribeIamList());
        log.info("ResourceResultData!!: {}", resourceResultData);

        log.info("IAM : {}, GroupScan : {}", combinedRequestDto.getResourceFilterDto().getIam(), combinedRequestDto.getResourceFilterDto().getScanGroup());
        log.info("pIndex : {}, pSize : {}", combinedRequestDto.getResourceFilterDto().getPageIndex(), combinedRequestDto.getResourceFilterDto().getPageSize());

        ResourceListDto allResources = resourceService.getAllResources(combinedRequestDto.getResourceFilterDto());

        Map<String, Object> response = new HashMap<>();
        response.put("resourceResultData", resourceResultData);
        response.put("allResources", allResources);

        log.info("response : {}", response.size());

        return ResponseEntity.ok(response);
    }
//    @GetMapping("/list")
//    public ResponseEntity<ResourceListDto> getResources(@RequestBody ResourceFilterRequestDto resourceFilterDto) throws Exception {
//        log.info("IAM : {}, GroupScan : {}", resourceFilterDto.getIam(), resourceFilterDto.getScanGroup());
//        log.info("pIndex : {}, pSize : {}", resourceFilterDto.getPageIndex(), resourceFilterDto.getPageSize());
//
//        ResourceListDto allResources = resourceService.getAllResources(resourceFilterDto);
//        return ResponseEntity.ok(allResources);
//    }

    /**
     * IAM 선택과 scanGroup을 필터링하여 조회하고 스캔 시작하는 API
     */
//    @PostMapping("/startScan")
//    public ResponseEntity<List<ResourceResultData>> saveDescribe(@RequestBody List<DescribeIamDto> describeIamList) throws Exception {
//        log.info("스캔 시작");
//        List<ResourceResultData> resourceResultData = resourceService.startDescribe(describeIamList);
//
//        return ResponseEntity.ok(resourceResultData);
//    }

    /**
     * 스캔 시작 후 resource와 service 필터
     */
}
