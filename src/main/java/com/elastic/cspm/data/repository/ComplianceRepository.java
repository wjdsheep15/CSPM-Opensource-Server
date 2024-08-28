package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.ComplianceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplianceRepository  extends JpaRepository<ComplianceResult, Long> {
    Optional<List<ComplianceResult>> findDistinctTop10ByOrderByScanTimeDesc();
}
