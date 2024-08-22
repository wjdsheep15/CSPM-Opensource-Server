package com.elastic.cspm.service;

import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.repository.IamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IamService {
    private final IamRepository iamRepository;


    public List<String> getIAMNicknames() {
        return iamRepository.findAll()
                .stream()
                .map(IAM::getNickName)
                .collect(Collectors.toList());
    }
}
