package com.elastic.cspm.repository;

import com.elastic.cspm.data.dto.QResourceDto;
import com.elastic.cspm.data.dto.ResourceFilterDto;
import com.elastic.cspm.data.dto.ResourceResultDto;
import com.elastic.cspm.data.entity.DescribeResult;
import com.elastic.cspm.data.entity.QDescribeResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ResourceRepository extends JpaRepository<DescribeResult, String>, ResourceRepositoryCustom {

}
