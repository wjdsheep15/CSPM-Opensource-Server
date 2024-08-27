package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, String> {
    List<Policy> findPolicyByPatternAndGroupName(String pattern, String groupName);

    Policy findByGroupName(String groupName);
}
