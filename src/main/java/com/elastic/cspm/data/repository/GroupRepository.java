package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.ScanGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<ScanGroup, String> {
}
