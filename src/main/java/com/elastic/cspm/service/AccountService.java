package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.MemberDto;
import com.elastic.cspm.data.entity.Member;
import com.elastic.cspm.data.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final MemberRepository memberRepository;

    public boolean signup(MemberDto memberDto) {
        Member member = new Member();
        member.setEmail(memberDto.getEmail());
        member.setPassword(memberDto.getPassword());
        member.setIamName(memberDto.getIamName());
        member.setAccountId(memberDto.getAccountId());
        member.setRole("user");
        member.setCreateAt(LocalDateTime.now());
        member.setUpdateAt(LocalDateTime.now());
        memberRepository.save(member);
        return true;
    }
}
