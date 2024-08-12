package com.elastic.cspm.data.repository;


import com.elastic.cspm.data.entity.IAM;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IamRepository extends JpaRepository<IAM, Long> {
    Optional<IAM> findEmailByAccessKey(String accessKey);

    Optional<IAM> findAllByAccessKey(String accessKey);
}
