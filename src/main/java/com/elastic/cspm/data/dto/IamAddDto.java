package com.elastic.cspm.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/*
setting 페이지에서 iam 추가 시 사용하는 Dto
 */
@Getter
@Setter
public class IamAddDto {
    @NotBlank(message = "Access Key는 필수입니다.")
    private String accessKey;

    @NotBlank(message = "Secret Key는 필수입니다.")
    private String secretKey; // 필드 이름 수정

    @NotBlank(message = "지역은 필수입니다.")
    private String region;

    @NotBlank(message = "Nick Name은 필수입니다.")
    private String nickname;

}
