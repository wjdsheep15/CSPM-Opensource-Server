package com.elastic.cspm.data.repository;

import com.elastic.cspm.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
}
