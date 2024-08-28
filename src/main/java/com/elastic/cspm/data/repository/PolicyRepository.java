package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.BridgeEntity;
import com.elastic.cspm.data.entity.Member;
import com.elastic.cspm.data.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    List<Policy> findByResourceName(String resourceName);
}
