package com.pawradar.api.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface WalkerRepository extends JpaRepository<Walker, Long> {

    /**
     * 내 위치 기준 반경 N미터 내의 산책자 조회 (거리순 정렬)
     * ST_Distance_Sphere(p1, p2): 두 지점 간의 구(지구)면 거리 계산 (단위: 미터)
     * POINT(경도, 위도): 순서 주의! (Longitude, Latitude)
     */
    @Query(value = "SELECT w.*, " +
                   "ST_Distance_Sphere(POINT(:lon, :lat), POINT(w.longitude, w.latitude)) as distance " +
                   "FROM walker w " +
                   "WHERE w.is_available = true " +
                   "HAVING distance <= :radius " + // 반경(미터) 필터링
                   "ORDER BY distance ASC",        // 가까운 순 정렬
           nativeQuery = true)
    List<Walker> findNearWalkers(
            @Param("lat") Double myLatitude,
            @Param("lon") Double myLongitude,
            @Param("radius") Double radiusInMeters
    );
}