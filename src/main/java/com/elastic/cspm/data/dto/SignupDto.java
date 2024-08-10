package com.elastic.cspm.data.dto;

import lombok.Getter;

@Getter
public class SignupDto {
    private String email;
    private String password;
    private String accessKey;
    private String SecretKey;
    private String region;
    private String accountId;
    private String userName;
}
