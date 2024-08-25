package com.elastic.cspm.config;

import com.elastic.cspm.data.repository.MemberRepository;
import com.elastic.cspm.data.repository.RefreshRepository;
import com.elastic.cspm.jwt.CustomLogoutFilter;
import com.elastic.cspm.jwt.JWTFilter;
import com.elastic.cspm.jwt.JWTUtil;
import com.elastic.cspm.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // 스프링 스큐리티 필터가 스프링 필터체인에 등록이 됩니다.
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors((cors -> cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        // 헤더 cors에 허용
                        configuration.setExposedHeaders(Arrays.asList("access", "Set-Cookie"));
                        return configuration;
                    }
                })) );

        //csrf disable
        http
                .csrf(AbstractHttpConfigurer::disable);
        // From 로그인 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        // 경로별 인가 작업
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        .requestMatchers("/api/account/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/reissue").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository, memberRepository), UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(new CustomLogoutFilter(refreshRepository, jwtUtil), LogoutFilter.class);
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}