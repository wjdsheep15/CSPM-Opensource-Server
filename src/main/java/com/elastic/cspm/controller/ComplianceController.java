package com.elastic.cspm.controller;
import com.elastic.cspm.data.dto.ComplianceResponseDto;
import com.elastic.cspm.data.entity.ComplianceResult;
import com.elastic.cspm.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(value = "/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceService complianceService;

    @GetMapping("/{iamNickName}/{groupName}")
    public  ResponseEntity<List<ComplianceResult>> complianceScan(@PathVariable  String iamNickName, @PathVariable String groupName){
        ResponseEntity<List<ComplianceResult>> complianceResultList = ResponseEntity.ok(complianceService.complianceScan(iamNickName, groupName));
        return complianceResultList;
    }
}
