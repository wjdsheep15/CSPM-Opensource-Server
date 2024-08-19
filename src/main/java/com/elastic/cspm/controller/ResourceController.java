package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.ResourceFilterDto;
import com.elastic.cspm.data.dto.ResourceResultDto.*;
import com.elastic.cspm.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping("/list")
    public ResponseEntity<ResourceListDto> getResources(@ModelAttribute ResourceFilterDto resourceFilterDto) throws Exception {
        log.info("getResources: IAM = {}, scanGroup = {}, ResourceID = {}, Service = {}",
                resourceFilterDto.getIAM(), resourceFilterDto.getScanGroup(),
                resourceFilterDto.getResource(), resourceFilterDto.getService());

        ResourceListDto allResources = resourceService.getAllResources(resourceFilterDto);
        return ResponseEntity.ok(allResources);
    }
}
