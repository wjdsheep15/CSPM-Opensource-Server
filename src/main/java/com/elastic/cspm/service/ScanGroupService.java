package com.elastic.cspm.service;

import com.elastic.cspm.data.entity.BridgeEntity;
import com.elastic.cspm.data.entity.ScanGroup;
import com.elastic.cspm.data.repository.BridgeEntityRepository;
import com.elastic.cspm.data.repository.ScanGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScanGroupService {
    private final ScanGroupRepository groupRepository;
    private final BridgeEntityRepository bridgeEntityRepository;

    public List<String> getScanGroup() {
        return groupRepository.findAll()
                .stream()
                .map(ScanGroup::getResourceGroupName)
                .collect(Collectors.toList());
    }


    public List<String> getScanGroupName(String email) {
        List<BridgeEntity> bridgeEntityList = bridgeEntityRepository.findAllByMemberEmail(email).orElse(null);

        if (bridgeEntityList == null) {
            return Collections.emptyList();
        }

        List<String> list = bridgeEntityList.stream()
                .map(bridgeEntity -> bridgeEntity.getScanGroup().getResourceGroupName())
                .toList();
        return list;
    }
}
