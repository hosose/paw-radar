package com.pawradar.api;

import com.pawradar.api.domain.*;
import com.pawradar.api.dto.WalkerResponse;
import com.pawradar.api.service.MatchingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class PawRadarApplicationTests {

    @Autowired WalkerRepository walkerRepository;
    @Autowired MatchingService matchingService;

    @Test
    void 회원가입_로그인_테스트() {
        // 1. 가입 시도
        matchingService.join("새유저", "testUser", "1234", 37.0, 127.0, DogSize.SMALL);
        
        // 2. 로그인 성공 케이스
        boolean success = matchingService.login("testUser", "1234");
        System.out.println("로그인 결과(성공해야함): " + success);
        
        // 3. 로그인 실패 케이스
        boolean fail = matchingService.login("testUser", "wrongPw");
        System.out.println("로그인 결과(실패해야함): " + fail);
    }
    
    @Test
    @Transactional // 테스트 끝나면 데이터 롤백 (깔끔하게)
    void 매칭_알고리즘_테스트() {
        // 1. 데이터 준비 (서울 강남역 주변)
        // 나 (강남역, 소형견 견주)
        Walker me = walkerRepository.save(new Walker("나(소형견)", 37.4979, 127.0276, true, DogSize.SMALL));
        
        // 친구 A (신논현역 - 가까움, 소형견 - 궁합 좋음) -> 1등 예상
        walkerRepository.save(new Walker("친구A(가깝+소형)", 37.5022, 127.0242, true, DogSize.SMALL));
        
        // 친구 B (역삼역 - 적당함, 대형견 - 궁합 보통) -> 2등 예상
        walkerRepository.save(new Walker("친구B(적당+대형)", 37.5006, 127.0364, true, DogSize.LARGE));

        // 친구 C (부산 - 멈) -> 추천 안 되어야 함
        walkerRepository.save(new Walker("친구C(부산)", 35.1795, 129.0756, true, DogSize.SMALL));

        // 2. 서비스 실행
        System.out.println("====== 🚀 매칭 시작 ======");
        List<WalkerResponse> result = matchingService.recommendWalkers(me.getId(), me.getLatitude(), me.getLongitude());

        // 3. 결과 출력
        for (WalkerResponse res : result) {
            System.out.println("추천 대상: " + res.getName());
            System.out.println("   - 거리: " + res.getDistanceMeters() + "m");
            System.out.println("   - 견종: " + res.getDogSize());
            System.out.println("   - 점수: " + res.getMatchScore() + "점 🔥");
            System.out.println("-------------------------");
        }
        System.out.println("==========================");
    }
}