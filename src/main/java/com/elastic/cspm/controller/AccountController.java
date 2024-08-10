package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.InfoResponseDto;
import com.elastic.cspm.data.dto.SignupDto;
import com.elastic.cspm.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/info")
    public ResponseEntity<InfoResponseDto> getAccounts(@RequestParam String accessKey, @RequestParam String secretKey, @RequestParam String region) {
        if (accessKey == null || secretKey == null || region == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request
        }

        InfoResponseDto infoResponseDto = accountService.getAwsAccountId(accessKey, secretKey, region);

        if (infoResponseDto == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }

        System.out.println(infoResponseDto.getAccountId());

        if (infoResponseDto.getStatus() == 0) {
            return ResponseEntity.ok(infoResponseDto); // 200 OK
        } else if (infoResponseDto.getStatus() == 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden 인증은 성공 권한 문제
        }else {
            return ResponseEntity.status(HttpStatus.GONE).build(); // 410 Gone
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupDto signupDto) {
        if(accountService.signup(signupDto)) {
            return ResponseEntity.ok("signup success");
        }
        return ResponseEntity.badRequest().build();
    }


}
