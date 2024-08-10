package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, String> {
}
