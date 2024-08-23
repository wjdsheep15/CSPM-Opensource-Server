package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.ScanGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<ScanGroup, String> {
    List<ScanGroup> findAll();
}
