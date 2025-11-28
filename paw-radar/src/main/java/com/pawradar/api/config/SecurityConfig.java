package com.pawradar.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // 개발 편의상 CSRF 끔
            .authorizeHttpRequests(auth -> auth
                // 1. 누구나 들어갈 수 있는 곳 (로그인, 가입, 화면파일, 스웨거)
                .requestMatchers("/", "/index.html", "/list.html", "/js/**", "/css/**").permitAll()
                .requestMatchers("/api/walkers/join", "/api/walkers/login").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // API 명세서도 허용
                
                // 2. 그 외 모든 요청(추천 API 등)은 "인증(로그인) 필요"
                .anyRequest().authenticated()
            )
         // 인증 실패 시 로그인 페이지로 강제 이동
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                	response.sendError(403);
                })
            )
            .formLogin(AbstractHttpConfigurer::disable); // 기본 로그인 폼 끄기

        return http.build();
    }
}