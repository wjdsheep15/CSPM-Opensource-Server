package com.elastic.cspm.data.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InfoResponseDto {
    private String accountId;
    private String userName;

    // 0 = 정상적, 1= 권한문제, 2, 오타
    private Integer status;


}
