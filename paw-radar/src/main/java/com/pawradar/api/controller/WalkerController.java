package com.pawradar.api.controller;

import com.pawradar.api.domain.DogSize;
import com.pawradar.api.dto.WalkerResponse;
import com.pawradar.api.service.MatchingService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/walkers")
public class WalkerController {

    @Autowired // 서비스 연결 (Lombok 없이 안전하게 주입)
    private MatchingService matchingService;

    // 1. 회원가입 API (POST /api/walkers/join)
    @PostMapping("/join")
    public ResponseEntity<String> join(
            @RequestParam("name") String name,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("lat") Double lat,
            @RequestParam("lon") Double lon
    ) {
        // 편의상 소형견으로 고정해서 가입
        matchingService.join(name, username, password, lat, lon, DogSize.SMALL);
        return ResponseEntity.ok("가입 성공!");
    }

    
 // 로그인이 되어있다면 200 OK, 안 되어있다면 403 Forbidden이 뜹니다.
    @GetMapping("/me")
    public ResponseEntity<String> checkAuth() {
        return ResponseEntity.ok("로그인 상태입니다.");
    }
    
    // 2. 로그인 테스트 API (POST /api/walkers/login)
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session
    ) {
        boolean success = matchingService.login(username, password);
        
        if (success) {
        	UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            );
            // 시큐리티 컨텍스트에 저장 (이제 서버가 이 사람을 기억합니다!)
            SecurityContextHolder.getContext().setAuthentication(token);
            
            // 세션에 강제로 저장 (이게 있어야 다음 요청 때도 기억함)
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        	
            return ResponseEntity.ok("로그인 성공! (비밀번호 일치)");
        } else {
            return ResponseEntity.status(401).body("로그인 실패 (아이디 혹은 비번 틀림)");
        }
    }
    
    // API 주소: GET /api/walkers/recommend?myId=1&lat=37.4979&lon=127.0276
    @GetMapping("/recommend")
    public ResponseEntity<List<WalkerResponse>> recommend(
    		@RequestParam("myId") Long myId,      // 내 ID
            @RequestParam("lat") Double lat,     // 내 현재 위도
            @RequestParam("lon") Double lon      // 내 현재 경도
    ) {
        // 아까 만든 '매칭 엔진' 가동!
        List<WalkerResponse> result = matchingService.recommendWalkers(myId, lat, lon);
        
        return ResponseEntity.ok(result);
    }
}