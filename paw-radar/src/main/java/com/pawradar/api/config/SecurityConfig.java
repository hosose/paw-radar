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
        .csrf(AbstractHttpConfigurer::disable) // CSRF 보안 끄기
        .authorizeHttpRequests(auth -> auth
            // ▼▼▼ 여기를 수정했습니다! ▼▼▼
            // "어떤 요청이든(anyRequest) 다 허용해라(permitAll)"
            .anyRequest().permitAll()
        )
        .formLogin(AbstractHttpConfigurer::disable);

    return http.build();
    }
}