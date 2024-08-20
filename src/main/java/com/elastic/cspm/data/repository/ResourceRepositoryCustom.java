package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.dto.QResourceDto;
import com.elastic.cspm.data.dto.ResourceFilterRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepositoryCustom {
    Page<QResourceDto> findResourceList (
            Pageable pageable,
            ResourceFilterRequestDto resourceFilterDto
    );
}

