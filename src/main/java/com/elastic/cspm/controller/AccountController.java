package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.MemberDto;
import com.elastic.cspm.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/")
    public ResponseEntity<String> addAccount() {
        return ResponseEntity.ok("hello");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberDto memberDto) {
        if(accountService.signup(memberDto)) {
            return ResponseEntity.status(205).body("회원가입 성공");
        }
        return ResponseEntity.status(405).body("회원가입 실패");
    }


}
