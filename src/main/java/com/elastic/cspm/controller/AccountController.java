package com.elastic.cspm.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/account")
public class AccountController {

    @PostMapping()
    public String addAccount() {
        return "success";
    }
}
