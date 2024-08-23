package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.ScanGroupDto;
import com.elastic.cspm.data.entity.BridgeEntity;
import com.elastic.cspm.data.entity.ScanGroup;
import com.elastic.cspm.data.repository.BridgeEntityRepository;
import com.elastic.cspm.data.repository.ScanGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ScanGroupRepository scanGroupRepository;
    private final BridgeEntityRepository bridgeEntityRepository;

    public List<ScanGroupDto> getScanGroup(String email){

       List<BridgeEntity> bridgeEntityList = bridgeEntityRepository.findAllByMemberEmail(email).orElseThrow();
       if(bridgeEntityList.isEmpty()){
           return new ArrayList<>();
       }

        List<ScanGroup> scanGroupStream = bridgeEntityList.stream()
                .map(bridgeEntity -> scanGroupRepository.findByResourceGroupName(bridgeEntity.getScanGroup().getResourceGroupName())
                        .orElse(null))
                .filter(scanGroup -> scanGroup != null)
                .toList();

        List<ScanGroupDto> scanGroupDtos = scanGroupStream.stream().map(ScanGroupDto::of).toList();

        return scanGroupDtos;
    }
}
