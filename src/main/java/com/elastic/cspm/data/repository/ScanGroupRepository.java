package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.ScanGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScanGroupRepository extends JpaRepository<ScanGroup, String> {
    Optional<ScanGroup> findByResourceGroupName(String name);
}
