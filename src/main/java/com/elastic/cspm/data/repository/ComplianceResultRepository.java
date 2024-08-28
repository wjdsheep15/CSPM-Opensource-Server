package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.ComplianceResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplianceResultRepository extends JpaRepository<ComplianceResult, Long>, ComplianceResultRepositoryCustom {
}
