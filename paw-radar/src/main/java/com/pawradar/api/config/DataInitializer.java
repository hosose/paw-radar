package com.pawradar.api.config;

import com.pawradar.api.domain.DogSize;
import com.pawradar.api.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private MatchingService matchingService;

    @Override
    public void run(String... args) throws Exception {
        // 1. 나 (강남역) - ID: 1번 예상
        Long myId = matchingService.join("나", "me", "1234", 37.4979, 127.0276, DogSize.SMALL);
        
        // 2. 친구A (신논현역 - 가까움)
        matchingService.join("친구A", "friend1", "1234", 37.5022, 127.0242, DogSize.SMALL);
        
        // 3. 친구B (부산 - 아주 멈)
        matchingService.join("부산친구", "friend2", "1234", 35.1795, 129.0756, DogSize.LARGE);

        System.out.println("=========================================");
        System.out.println("🎉 샘플 데이터 생성 완료!");
        System.out.println("내 ID는 [" + myId + "] 입니다.");
        System.out.println("테스트 URL: http://localhost:8080/api/walkers/recommend?myId=" + myId + "&lat=37.4979&lon=127.0276");
        System.out.println("=========================================");
    }
}