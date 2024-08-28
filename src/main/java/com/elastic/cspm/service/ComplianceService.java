package com.elastic.cspm.service;

import com.elastic.cspm.data.entity.*;
import com.elastic.cspm.data.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ComplianceService {

    private final DescribeResultRepository describeResultRepository;
    private final IamRepository iamRepository;
    private final ComplianceResultRepository complianceResultRepository;
    private final PolicyRepository policyRepository;


    public void complianceScan(String iamNickName, String groupName) {

        IAM iam = iamRepository.findIAMByNickName(iamNickName);
        /** describe result에서 특정 IAM의, Resource TargetList 불러오는 메소드 */
        //List<String> scanTargetList = getScanTargetList(iam, groupName);
        List<DescribeResult> describeResultList = describeResultRepository.findByIamAndGroupName(iam,groupName);

        /** 취약점 패턴 가져 오는 메소드 패턴들의 대한 값들 그룹*/
        List<Policy> policyList = policyRepository.findByResourceName(groupName);
        /**쿼리 DSL을 통해 Pattern과 ScanTarget 값을 비교해서 ComplianceResult에 저장하는 메소드*/
        complianceResultRepository.saveComplianceResultsFromDescribeAndPolicy(iam, describeResultList, policyList);


    }






//    // 취약점 검사할 Scan Data 불러오기
//    public List<DescribeResult> getScanTargetList( IAM iam,String groupName){
//        // nickName으로로 IamId 불러오기
//        // iamID를 통해  DescribeResult 테이블 데이터 불러오기
//        List<DescribeResult> decribeListList = describeResultRepository.findByIamAndGroupName(iam,groupName);
//
//        return decribeListList;
//    }



}
