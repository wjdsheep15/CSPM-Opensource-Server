package com.elastic.cspm.data.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupDto {
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "Access Key는 필수입니다.")
    private String accessKey;

    @NotBlank(message = "Secret Key는 필수입니다.")
    private String secretKey; // 필드 이름 수정

    @NotBlank(message = "지역은 필수입니다.")
    private String region;

    @NotBlank(message = "Account ID는 필수입니다.")
    private String accountId;

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String userName;
}
