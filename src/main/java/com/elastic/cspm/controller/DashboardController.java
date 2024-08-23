package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.ScanGroupDto;
import com.elastic.cspm.jwt.JWTUtil;
import com.elastic.cspm.service.DashboardService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final JWTUtil jwtUtil;

    /**
     * scanGroup 데이터 가져오기
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/group")
    public ResponseEntity<List<ScanGroupDto>> getGroup(HttpServletRequest request, HttpServletResponse response){
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            log.info("refresh 토큰이 없습니다.");
            return  ResponseEntity.status(400).build();
        }
        String email = jwtUtil.getUsername(refresh);

        List<ScanGroupDto> scanGroupDtos = dashboardService.getScanGroup(email);
        if (scanGroupDtos.isEmpty()) {
            log.info(email + "의 scanGroup을 못 가져왔습니다.");
            return  ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(scanGroupDtos);
    }

}
