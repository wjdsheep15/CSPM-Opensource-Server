package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.InfoResponseDto;
import com.elastic.cspm.data.dto.SignupDto;
import com.elastic.cspm.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/validation/iam")
    public ResponseEntity<InfoResponseDto> validationIam(@RequestParam String accessKey, @RequestParam String secretKey, @RequestParam String region) {
        if (accessKey == null || secretKey == null || region == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request
        }

        InfoResponseDto infoResponseDto = accountService.validationAwsAccountId(accessKey, secretKey, region);

        if (infoResponseDto == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }

        if (infoResponseDto.getStatus() == 0) {
            return ResponseEntity.ok(infoResponseDto); // 200 OK
        } else if (infoResponseDto.getStatus() == 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden 인증은 성공 권한 문제
        }else {
            return ResponseEntity.status(HttpStatus.GONE).build(); // 410 Gone
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupDto signupDto) {
        boolean isSignupSuccessful = accountService.signup(signupDto);
        return isSignupSuccessful
                ? ResponseEntity.ok("signup success")
                : ResponseEntity.badRequest().body("회원가입 실패");
    }

    @GetMapping("/validation/email")
    public ResponseEntity<Map<String, String>> validationEmail(@RequestParam String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "이메일을 입력해주세요."));
        }

        String isValidEmail = accountService.validationEmail(email);
        if (isValidEmail.length() == 6) {
            Map<String, String> response = new HashMap<>();
            response.put("verificationCode", isValidEmail); // JSON 키와 값 추가
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.GONE).body(Map.of("error", "이메일 인증 실패")); // 410
        }
    }


}
