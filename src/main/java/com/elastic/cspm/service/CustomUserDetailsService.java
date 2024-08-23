package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.CustomUserDetails;
import com.elastic.cspm.data.entity.Member;
import com.elastic.cspm.data.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member memberData = memberRepository.findByEmail(username).orElse(null);

        if (memberData != null) {
            return new CustomUserDetails(memberData);
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
