package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, String> {
//    findPolicyByPatternAndGroupName(String pattern, String groupName);
}
