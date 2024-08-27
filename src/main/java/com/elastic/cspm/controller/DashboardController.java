package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.GraphScanDto;
import com.elastic.cspm.data.dto.ResponseScanGroupDto;
import com.elastic.cspm.data.dto.ScanGroupDto;
import com.elastic.cspm.jwt.JWTUtil;
import com.elastic.cspm.service.DashboardService;
import com.elastic.cspm.service.RefreshService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final RefreshService refreshService;
    private final JWTUtil jwtUtil;

    /**
     * scanGroup 데이터 가져오기
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/group")
    public ResponseEntity<List<ScanGroupDto>> getGroup(HttpServletRequest request, HttpServletResponse response){

        String email = refreshService.getEmail(request);

        List<ScanGroupDto> scanGroupDtos = dashboardService.getScanGroup(email);
        if (scanGroupDtos.isEmpty()) {
            log.info(email + "의 scanGroup을 못 가져왔습니다.");
            return  ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(scanGroupDtos);
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> postGroup(HttpServletRequest request,@Valid @RequestBody ResponseScanGroupDto responseScanGroupDto){

        String email = refreshService.getEmail(request);
        if(email == null){
            return ResponseEntity.status(400).body(Map.of("result", "not refresh Token"));
        }

        if(responseScanGroupDto== null){
            return ResponseEntity.status(404).body(Map.of("result", "not ResponseScanGroupDto"));
        }
        Boolean result = dashboardService.saveGroup(email, responseScanGroupDto);
        if (result) {
            return ResponseEntity.ok(Map.of("result", "success"));
        }else{
            return ResponseEntity.status(404).body(Map.of("result", "service fail"));
        }
    }

    @DeleteMapping("/{groupName}")
    public ResponseEntity<Map<String, String>> deleteGroup( @PathVariable String groupName){

        Boolean result = dashboardService.deleteGroup(groupName);
        if (result) {
            return ResponseEntity.ok(Map.of("result", "success"));
        }else{
            return ResponseEntity.status(404).body(Map.of("result", "service fail"));
        }
    }

    /**
     * 스캔 그룹 데이터 보내기
     * @param groupName
     * @return
     */
    @GetMapping("/graph/{groupName}")
    public ResponseEntity<List<GraphScanDto>> getGroupGraph(@PathVariable String groupName){
        List<GraphScanDto> graphScanDtosList = dashboardService.getScanGraphData(groupName);
        if (graphScanDtosList ==null || graphScanDtosList.isEmpty()){
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(graphScanDtosList);
    }
}
