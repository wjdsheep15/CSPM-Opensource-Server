package com.elastic.cspm.service;

import com.elastic.cspm.data.entity.DescribeResult;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.repository.DescribeResultRepository;
import com.elastic.cspm.data.repository.IamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplianceService {

    private final DescribeResultRepository describeResultRepository;
    private final IamRepository iamRepository;

    // 취약점 검사할 Scan Data 불러오기
    public List<String> getScanTargetList(String iamNickname,String groupName){
        // nickName으로로 IamId 불러오기
        IAM iam = iamRepository.findIAMByNickName(iamNickname);

        // iamID를 통해  DescribeResult 테이블 데이터 불러오기
        List<DescribeResult> decribeList = describeResultRepository.findByIamAndGroupName(iam,groupName);
        List<String> scanTargetList = new ArrayList<>();
        for (DescribeResult describeResult : decribeList) {
            scanTargetList.add(describeResult.getScanTarget());
        }
        return scanTargetList;
    }
}
