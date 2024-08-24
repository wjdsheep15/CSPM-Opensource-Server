package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.DescribeResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ResourceRepository extends JpaRepository<DescribeResult, String>, ResourceRepositoryCustom {

}
