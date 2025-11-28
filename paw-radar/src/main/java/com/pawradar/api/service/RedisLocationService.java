package com.pawradar.api.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisLocationService {

	@Autowired
    private StringRedisTemplate redisTemplate;
    
    // Redis에 저장할 키 이름 (폴더 같은 개념)
    private static final String GEO_KEY = "walkers:location"; 

    /**
     * 실시간 위치 업데이트
     * @param userId 사용자 ID
     * @param lat 위도
     * @param lon 경도
     */
    public void updateLocation(Long userId, Double lat, Double lon) {
        String memberId = String.valueOf(userId);

        // 1. GEO 자료구조에 내 위치 저장 (핵심!)
        // redis-cli 명령어: GEOADD walkers:location 127.0 37.0 "1"
        redisTemplate.opsForGeo().add(GEO_KEY, new Point(lon, lat), memberId);

        // 2. (선택) 생존 신고 - "나 지금 접속 중이야!"
        // 별도의 키에 만료시간(TTL)을 걸어서 관리하면, 
        // 1분 동안 위치 전송이 없으면 "접속 끊김"으로 처리할 수 있습니다.
        redisTemplate.opsForValue().set("active:" + memberId, "ON", 1, TimeUnit.MINUTES);
        
        System.out.println("📍 위치 업데이트 완료: User " + userId + " -> " + lat + ", " + lon);
    }
}