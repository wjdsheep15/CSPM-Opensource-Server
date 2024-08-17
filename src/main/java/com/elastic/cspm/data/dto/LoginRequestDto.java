package com.elastic.cspm.data.dto;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String email;
    private String password;
}
