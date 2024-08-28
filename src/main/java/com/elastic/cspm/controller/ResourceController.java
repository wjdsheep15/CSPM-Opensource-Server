package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.IAMScanGroupResponseDto;
import com.elastic.cspm.data.dto.ResourceFilterRequestDto;
import com.elastic.cspm.data.dto.ResourceResultData;
import com.elastic.cspm.data.dto.ResourceResultResponseDto;
import com.elastic.cspm.service.IamService;
import com.elastic.cspm.service.RefreshService;
import com.elastic.cspm.service.ResourceService;
import com.elastic.cspm.service.ScanGroupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {
    private final ResourceService resourceService;
    private final IamService iamService;
    private final ScanGroupService scanGroupService;
    private final RefreshService refreshService;

    /**
     * ScanGroup 선택 API
     */
    @GetMapping("/scangroup")
    public ResponseEntity<List<String>> getScanGroupName(HttpServletRequest request) {
        String email = refreshService.getEmail(request);

        if(email == null || email.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        try {
            List<String> scanGroups = scanGroupService.getScanGroupName(email);
            return ResponseEntity.ok(scanGroups);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * IAM 선택과 ScanGroup을 같은 API에.
     * 이렇게 한다면 IamSelectDto, ScanGroupSelectDto 삭제.
     */
    @GetMapping("/iam-scanGroup")
    public ResponseEntity<IAMScanGroupResponseDto> getIAMAndScanGroupNames(HttpServletRequest request) {

        String email = refreshService.getEmail(request);

        if(email == null || email.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        // IAM Nicknames 가져오기
        List<String> iamNicknames = iamService.getIAMNicknames(email);
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
    public ResponseEntity<Map<String, Object>> getResourcesAndStartScan(@RequestBody ResourceFilterRequestDto resourceFilterRequestDto) throws Exception {
        log.info("스캔 시작");

        if (resourceFilterRequestDto == null) {
            throw new IllegalArgumentException("IAM과 그룹을 선택해야 합니다.");
        }

        // iam과 group 선택하지 않으면 에러가 발생.
        List<ResourceResultData> resourceResultData = resourceService.startDescribe(resourceFilterRequestDto);
        log.info("ResourceResultData!!: {}", resourceResultData);
        log.info("스캔 끝");

        // 필터링 리스트 조회
        // 조회하는 부분 엔티티 추가돼서 쿼리 수정 필요.
        ResourceResultResponseDto.ResourceListDto allResources = resourceService.getAllResources(resourceFilterRequestDto);

        Map<String, Object> response = new HashMap<>();
        response.put("resourceResultData", resourceResultData);
        response.put("allResources", allResources);

        log.info("resourceResultData : {}", resourceResultData);
        log.info("allResources : {}", allResources);
        log.info("response : {}", response.size());


        return ResponseEntity.ok(response);
    }
}
