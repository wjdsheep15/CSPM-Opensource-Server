package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.DescribeResult;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DescribeResultRepository extends JpaRepository<DescribeResult, Long> {
    List<DescribeResult> findByIamAndGroupName(IAM iam, String groupName);
}
