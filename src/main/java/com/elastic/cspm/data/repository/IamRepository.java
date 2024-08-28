package com.elastic.cspm.data.repository;


import com.elastic.cspm.data.dto.ResourceFilterRequestDto;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IamRepository extends JpaRepository<IAM, Long> {
    Optional<IAM> findEmailByAccessKey(String accessKey);

    Optional<IAM> findAllByAccessKey(String accessKey);

    IAM findIAMByNickName(String nickName);

    List<IAM> findAllByMemberEmail(String memberEmail);

    List<IAM> findAll();

}