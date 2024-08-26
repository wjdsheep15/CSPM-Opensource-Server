package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.IamSelectDto;
import com.elastic.cspm.data.dto.InfoResponseDto;
import com.elastic.cspm.service.IamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/iamsettings")
@RequiredArgsConstructor
public class IamsettingController {
    private final IamService iamService;

    @PostMapping

    @GetMapping("/validation/iam")
    public ResponseEntity<InfoResponseDto> addIamValidation(@RequestParam String accessKey, @RequestParam String secretKey, @RequestParam String region) {
        if (accessKey == null || secretKey == null || region == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request
        }

        InfoResponseDto infoResponseDto = iamService.validationIam(accessKey, secretKey, region);

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
    @DeleteMapping
    public ResponseEntity<Void> deleteIam(@RequestBody List<IamSelectDto> iamSelectDtoList){
        return iamService.iamDelete(iamSelectDtoList);
    }

}
