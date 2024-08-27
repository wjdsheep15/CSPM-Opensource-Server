package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.DescribeResult;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DescribeResultRepository extends JpaRepository<DescribeResult, Long> {
    List<DescribeResult> findByIamAndGroupName(IAM iam, String groupName);

    List<String> findByIam(IAM iam);
    Optional<DescribeResult> findTopByScanGroupOrderByIdDesc(String scanGroup);

    Optional<List<DescribeResult>> findAllByScanGroupAndScanTime(String scanGroup, LocalDateTime scamTime);
}
