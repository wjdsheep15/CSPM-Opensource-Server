package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);


//    Member findByAccountIdAndIamName(String accountId, String IamName);
}
