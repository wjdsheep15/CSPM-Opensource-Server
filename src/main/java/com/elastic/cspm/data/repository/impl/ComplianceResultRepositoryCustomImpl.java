package com.elastic.cspm.data.repository.impl;

import com.elastic.cspm.data.entity.ComplianceResult;
import com.elastic.cspm.data.entity.DescribeResult;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.entity.Policy;
import com.elastic.cspm.data.repository.ComplianceResultRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ComplianceResultRepositoryCustomImpl implements ComplianceResultRepositoryCustom {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void saveComplianceResultsFromDescribeAndPolicy(IAM iam, List<DescribeResult> describeResults, List<Policy> policies) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        for (DescribeResult describeResult : describeResults) {
            for (Policy policy : policies) {
                if (describeResult.getScanTarget().contains(policy.getPattern())) {
                    ComplianceResult complianceResult = new ComplianceResult();
                    complianceResult.setScanTime(LocalDateTime.now());
                    complianceResult.setStatus(false);  // 기본값으로 false 설정 (필요에 따라 변경)
                    complianceResult.setResourceId(describeResult.getResourceId());
                    complianceResult.setIam(iam);
                    complianceResult.setPolicy(policy);

                    entityManager.persist(complianceResult);
                }
            }
        }
    }
}
