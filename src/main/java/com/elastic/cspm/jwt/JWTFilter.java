package com.elastic.cspm.jwt;

import com.elastic.cspm.data.dto.CustomUserDetails;
import com.elastic.cspm.data.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 Authorization 헤더를 찾음
        String authorization= request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        System.out.println("authorization now");
        //Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        // 토큰 소멸 시간 검증
        System.out.println(jwtUtil.isExpired(token));
        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");

            String refreshToken = request.getHeader("Refresh-Token");
            if(refreshToken != null || !jwtUtil.isExpired(refreshToken)) {
                // 새로운 액세스 토큰 발급
                String username = jwtUtil.getUsername(refreshToken);
                String role = jwtUtil.getRole(refreshToken);
                String newAccessToken = jwtUtil.createJwt(username, role, 1800000L); // 필요한 역할 설정
                token = newAccessToken;
                response.setHeader("Authorization", "Bearer " + newAccessToken);
                response.setHeader("Refresh-Token", refreshToken);
                System.out.println("New access token issued : " + username);
            }else{
                // 리프레시 토큰이 없거나 만료된 경우, 401 Unauthorized 응답 반환
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Refresh token is missing or expired.");
                return;
            }
        }

        System.out.println("Token is valid");
        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        Member member = new Member();
        member.setEmail(username);
        member.setPassword("temppassword");
        member.setRole(role);

        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
