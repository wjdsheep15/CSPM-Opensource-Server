package com.elastic.cspm.data.repository;


import com.elastic.cspm.data.entity.DescribeResult;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.entity.Policy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceResultRepositoryCustom {
    void saveComplianceResultsFromDescribeAndPolicy(IAM iam, List<DescribeResult> describeResults, List<Policy> policies);


}
