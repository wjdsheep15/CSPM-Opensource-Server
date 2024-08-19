package com.elastic.cspm.repository;

import com.elastic.cspm.data.dto.QResourceDto;
import com.elastic.cspm.data.dto.ResourceFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepositoryCustom {
    Page<QResourceDto> findResourceList (
            Pageable pageable,
            ResourceFilterDto resourceFilterDto
    );
}

