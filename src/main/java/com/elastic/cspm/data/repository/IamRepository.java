package com.elastic.cspm.data.repository;


import com.elastic.cspm.data.entity.IAM;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IamRepository extends JpaRepository<IAM, Long> {
}
