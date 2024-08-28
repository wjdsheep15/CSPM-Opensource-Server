package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.ComplianceResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComplianceResultRepository extends JpaRepository<ComplianceResult, Long>, ComplianceResultRepositoryCustom {

    Optional<List<ComplianceResult>> findDistinctTop10ByOrderByScanTimeDesc();
}
