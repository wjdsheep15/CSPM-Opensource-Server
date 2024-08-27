package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.ComplianceResponseDto;
import com.elastic.cspm.data.entity.*;
import com.elastic.cspm.data.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ComplianceService {

    private final DescribeResultRepository describeResultRepository;
    private final IamRepository iamRepository;
    private final ComplianceRepository ComplianceRepository;
    private final MemberRepository memberRepository;
    private final PolicyRepository policyRepository;


    public void complianceScan(String iamNickName, String groupName) {

        IAM iam = iamRepository.findIAMByNickName(iamNickName);
        /** describe result에서 특정 IAM의, Resource TargetList 불러오는 메소드 */
        List<String> scanTargetList = describeResultRepository.findByIam(iam);

        /** 취약점 패턴 가져 오는 메소드*/
        Policy policy = policyRepository.findByGroupName(groupName);
        String pattern = policy.getPattern();



        // 취약점 결과 Entity에 저장
        Member member = iam.getMember();
        String email = member.getEmail();



        //Todo 취약점 스캔 완료 API 완성 후 수정 필요
        ComplianceResult complianceResult = new ComplianceResult(LocalDateTime.now(),true,"dsa",iam,policy);
        ComplianceRepository.save(complianceResult);


    }






    // 취약점 검사할 Scan Data 불러오기
//    public List<String> getScanTargetList(String iamNickname,String groupName){
//        // nickName으로로 IamId 불러오기
//        IAM iam = iamRepository.findIAMByNickName(iamNickname);
//
//        // iamID를 통해  DescribeResult 테이블 데이터 불러오기
//        List<DescribeResult> decribeList = describeResultRepository.findByIamAndGroupName(iam,groupName);
//        List<String> scanTargetList = new ArrayList<>();
//        for (DescribeResult describeResult : decribeList) {
//            scanTargetList.add(describeResult.getScanTarget());
//        }
//        return scanTargetList;
//    }



}
