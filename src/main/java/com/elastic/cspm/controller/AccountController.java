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

    /**
     * IAM 검증 EndPoint
     * @param accessKey
     * @param secretKey
     * @param region
     * @return
     */
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
        }else if (infoResponseDto.getStatus() == 2){
            return ResponseEntity.status(HttpStatus.GONE).build(); // 410 Gone
        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict 중복
        }
    }

    /**
     * 회원가입 EndPoint
     * @param signupDto
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupDto signupDto) {
        boolean isSignupSuccessful = accountService.signup(signupDto);
        return isSignupSuccessful
                ? ResponseEntity.ok("signup success")
                : ResponseEntity.badRequest().body("회원가입 실패");
    }

    /**
     * Email 검증 EndPoint
     * @param email
     * @return
     */
    @GetMapping("/validation/email")
    public ResponseEntity<Map<String, String>> validationEmail(@RequestParam String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "이메일을 입력해주세요."));
        }
        String isValidEmail = accountService.validationEmail(email);
        if (isValidEmail==null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "이메일 중복")); // 409 중복
        }
        Map<String, String> response = new HashMap<>();
        response.put("verificationCode", isValidEmail); // JSON 키와 값 추가
        return ResponseEntity.ok(response);
    }

    /**
     * Id 찾기 EndPoint
     * @param accessKey
     * @return
     */
    @GetMapping("/id/{accessKey}")
    public ResponseEntity<Map<String, String>> searchId(@PathVariable String accessKey) {
        Map<String, String> response = new HashMap<>();
        if (accessKey == null || accessKey.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String searchEmail = accountService.SearchEmail(accessKey);
        if(searchEmail == null || searchEmail.isEmpty()){
            response.put("error", "email not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404
        }
        response.put("email", searchEmail);
        return ResponseEntity.ok(response);

    }

    /**
     * password 찾기 EndPoint
     * @param email
     * @return
     */
    @GetMapping("/password/{email}")
    public ResponseEntity<Map<String, String>> searchPassword(@PathVariable String email) {
        Map<String, String> response = new HashMap<>();
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String password =  accountService.SearchPassword(email);
        if(password == null || password.isEmpty()){
            response.put("error", "password not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404
        }
        response.put("password", password);
        return ResponseEntity.ok(response);
    }
}
