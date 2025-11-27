package com.pawradar.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawradar.api.domain.DogSize;
import com.pawradar.api.domain.Walker;
import com.pawradar.api.domain.WalkerRepository;
import com.pawradar.api.dto.WalkerResponse;

@Service
//@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

	@Autowired
    private WalkerRepository walkerRepository;
	
	@Autowired // [추가] 암호화 도구 주입
    private PasswordEncoder passwordEncoder;

    // "내 ID, 내 위치(위/경도)"를 받아서 추천 목록 반환
    public List<WalkerResponse> recommendWalkers(Long myId, Double myLat, Double myLon) {
        
        // 1. 내 강아지 정보 조회 (가산점 로직을 위해 필요)
        Walker me = walkerRepository.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        // 2. 반경 3km 이내 사람들 조회 (DB의 Spatial Index 활용!)
        // LBS 핵심: DB에서 이미 거리가 계산되어 나옵니다.
        List<Walker> nearWalkers = walkerRepository.findNearWalkers(myLat, myLon, 3000.0);

        // 3. 점수 계산 및 정렬 (Java Stream 활용)
        return nearWalkers.stream()
                .filter(w -> !w.getId().equals(myId)) // "나"는 제외
                .map(target -> {
                    // 거리 계산 (DB가 계산해준 값 가져오기 - 여기선 데모라 직접 계산 로직을 넣거나 DB 결과 매핑 필요)
                    // *참고: 실제론 Repository DTO Projection을 쓰지만, 빠른 구현을 위해 하버사인 등으로 간략화하거나 DB값을 쓴다고 가정
                    // 여기서는 테스트 편의상 '단순 거리 계산'으로 점수를 매기겠습니다.
                    double distance = calculateDistance(myLat, myLon, target.getLatitude(), target.getLongitude());
                    
                    int score = 0;

                    // [알고리즘 1] 거리 점수: 3km(3000m)에서 가까울수록 점수 높음 (최대 30점)
                    score += (int) ((3000 - distance) / 100);

                    // [알고리즘 2] 궁합 점수: 강아지 크기가 같으면 +50점! (안전한 산책)
                    if (me.getDogSize() == target.getDogSize()) {
                        score += 50;
                    }

                    return new WalkerResponse(target, distance, score);
                })
                .sorted((w1, w2) -> Integer.compare(w2.getMatchScore(), w1.getMatchScore())) // 점수 높은 순 정렬
                .collect(Collectors.toList());
    }

    // 간단한 거리 계산식 (Haversine 공식 간소화 - 테스트용)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) 
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1609.344; // 미터 단위 변환
        return dist;
    }
    
 // [기능 1] 회원가입
    @Transactional
    public Long join(String name, String username, String rawPassword, Double lat, Double lon, DogSize dogSize) {
        // 1. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // 2. 객체 생성 (기존 생성자 활용)
        Walker walker = new Walker(name, lat, lon, true, dogSize);
        
        // 3. 로그인 정보 주입
        walker.setLoginInfo(username, encodedPassword);
        
        // 4. 저장
        walkerRepository.save(walker);
        return walker.getId();
    }

    // [기능 2] 로그인 (단순 검증용)
    public boolean login(String username, String rawPassword) {
        // 1. 아이디로 조회 (편의상 전체 검색 후 필터링 - 실제론 findByUsername 만들어야 함)
        List<Walker> all = walkerRepository.findAll();
        Walker user = all.stream()
                .filter(w -> w.getUsername() != null && w.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (user == null) return false; // 유저 없음

        // 2. 비밀번호 맞는지 확인 (rawPassword vs 암호화된 DB비밀번호)
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}