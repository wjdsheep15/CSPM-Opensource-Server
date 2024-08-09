package com.elastic.cspm.data.dto;

import com.elastic.cspm.data.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberDto {
    private String email;
    private String password;
    private String iamName;
    private String accountId;


    public static MemberDto of(Member member) {
        return new MemberDto(
                member.getEmail(),
                member.getPassword(),
                member.getIamName(),
                member.getAccountId()
        );
    }
}
