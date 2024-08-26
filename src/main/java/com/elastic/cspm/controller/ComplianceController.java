package com.elastic.cspm.controller;
import com.elastic.cspm.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceService complianceService;

    // 취약점 검사할 Scan Data 불러오기
    public List<String> getScanTargetList(String iamNickname,String groupName) {
        List<String> scanTargetList = complianceService.getScanTargetList(iamNickname,groupName);
        return scanTargetList;
    }
}
