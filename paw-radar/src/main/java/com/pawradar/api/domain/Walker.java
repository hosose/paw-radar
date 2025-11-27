package com.pawradar.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "walker", indexes = {
    @Index(name = "idx_walker_location", columnList = "longitude, latitude")
})
public class Walker {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [수정 1] 사라졌던 '이름(닉네임)' 필드 부활! (이게 없어서 에러가 난 겁니다)
    @Column(nullable = false)
    private String name; 

    // [수정 2] 로그인 아이디는 따로 관리
    private String username; 
    private String password;

    // LBS 데이터
    private Double latitude;
    private Double longitude;
    private boolean isAvailable;

    @Enumerated(EnumType.STRING)
    private DogSize dogSize;

    // 1. 기본 생성자 (JPA 필수)
    public Walker() {
    }

    // 2. 데이터 저장용 생성자 (수동 구현)
    // [수정 3] 생성자에서는 '이름'만 받고, 아이디/비번은 받지 않음 (분리)
    public Walker(String name, Double latitude, Double longitude, boolean isAvailable, DogSize dogSize) {
        this.name = name;           // <-- 이름을 여기에 저장해야 합니다!
        this.latitude = latitude;
        this.longitude = longitude;
        this.isAvailable = isAvailable;
        this.dogSize = dogSize;
    }

    // 3. 로그인 정보 입력 메서드
    public void setLoginInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // 4. Getter 메서드들 (수동 추가)
    public Long getId() { return id; }
    public String getName() { return name; }         // 이름 Getter
    public String getUsername() { return username; } // 아이디 Getter
    public String getPassword() { return password; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public boolean isAvailable() { return isAvailable; }
    public DogSize getDogSize() { return dogSize; }
}