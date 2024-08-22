package com.elastic.cspm.service;

import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.entity.ScanGroup;
import com.elastic.cspm.data.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScanGroupService {
    private final GroupRepository groupRepository;

    public List<String> getScanGroup() {
        return groupRepository.findAll()
                .stream()
                .map(ScanGroup::getResourceGroupName)
                .collect(Collectors.toList());
    }
}
