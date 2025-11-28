# 🐶 멍멍 레이더 (Paw Radar)
> **위치 기반(LBS) 반려견 산책 친구 실시간 매칭 서비스**

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7.x-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-24.0-2496ED?style=for-the-badge&logo=docker&logoColor=white)

## 📖 프로젝트 소개
**"내 주변에 나와 산책 성향이 맞는 친구는 없을까?"**
사용자의 실시간 위치(위도/경도)를 기반으로 반경 3km 이내의 산책 파트너를 탐색하고, **자체 매칭 알고리즘(거리+견종)**을 통해 최적의 상대를 추천해주는 백엔드 API 서비스입니다.

레거시 환경(Docker Toolbox)과 최신 기술 스택(Spring Boot 3.4)을 통합하는 과정에서 발생한 다양한 인프라 및 프레임워크 이슈를 해결하며 안정적인 백엔드 시스템을 구축했습니다.

## 🛠 기술 스택 (Tech Stack)

### Backend & Security
* **Core:** Java 17, Spring Boot 3.4.0
* **Security:** Spring Security (Custom AuthenticationFilter, BCrypt 암호화)
* **Database:** * **MySQL 8.0:** 회원 정보 및 매칭 데이터 영구 저장 (Spatial Index 활용)
    * **Redis:** 실시간 위치 정보(Geo) 고속 처리 및 캐싱
* **ORM:** Spring Data JPA (Hibernate 6.x)
* **API Docs:** SpringDoc (Swagger UI)

### Infrastructure
* **Environment:** Docker Toolbox (VirtualBox 기반 레거시 환경 대응)
* **Networking:** ngrok (로컬 서버 외부 터널링 및 HTTPS 모바일 테스트 환경 구축)

### Frontend (MVP)
* **Map Visualization:** Kakao Maps API
* **Client:** HTML5, Vanilla JavaScript (Geolocation API)

## 💡 핵심 구현 기능 (Key Features)

### 1. 🚀 Hybrid DB 아키텍처 (MySQL + Redis)
* **실시간성 강화:** 빈번하게 변하는 사용자 위치 정보(Update)는 Disk I/O 부하가 큰 MySQL 대신 **In-Memory DB인 Redis(GEO 자료구조)**로 처리하여 성능 극대화.
* **데이터 분리:** 휘발성 위치 데이터(Redis)와 영구 보관 데이터(MySQL)를 분리하여 시스템 안정성 확보.

### 2. 📍 고성능 LBS & 모바일 최적화
* **서버 사이드:** Redis `GEORADIUS` 및 MySQL `ST_Distance_Sphere`를 활용한 이중 반경 검색 구현.
* **클라이언트 사이드:** 모바일 배터리 효율과 서버 부하를 고려하여, **"이동 거리 10m 이상 + 5초 주기"** 조건 충족 시에만 위치를 전송하는 필터링 로직 구현.

### 3. 🧠 가중치 기반 매칭 알고리즘 (Weighted Scoring)
* 단순 거리순 정렬을 넘어선 사용자 경험(UX) 중심 알고리즘 구현.
    * **거리 점수:** 3km 내에서 가까울수록 고득점.
    * **궁합 점수:** 사용자의 반려견 크기(대/중/소)가 일치할 경우 가산점 부여.

### 4. 🔐 사용자 보안 시스템 (Spring Security)
* **인증/인가:** `SecurityFilterChain`을 커스터마이징하여 API 별 접근 권한 제어 (로그인 필수/예외 처리).
* **비밀번호 보안:** `BCryptPasswordEncoder`를 적용하여 단방향 암호화 저장.
* **세션 보안:** JSESSIONID 탈취 방지를 위한 HttpOnly 설정 및 세션 유효성 검증 로직 구현.

### 5. 🗺️ 시각화 및 문서화
* **Kakao Map 연동:** 추천된 산책 친구의 위치를 지도 마커(Marker)와 인포윈도우로 시각화.
* **Swagger 도입:** 프론트엔드 협업을 위한 API 명세서 자동화 (`/swagger-ui/index.html`).

## 🚀 트러블 슈팅 (Troubleshooting & Challenges)
> **개발 과정에서 발생한 주요 이슈와 해결 과정입니다.**

### 🔥 1. 모바일 환경에서의 GPS 보안 정책 이슈
* **문제:** PC(Localhost)에서는 작동하던 GPS 기능이 모바일 접속 시 차단됨 (HTTPS 필수 정책).
* **해결:** `ngrok`을 도입하여 로컬 서버에 HTTPS 터널링을 구축, 모바일 브라우저의 보안 정책을 준수하고 위치 권한 획득 성공.

### 🔥 2. Docker 환경과 OS 호환성 문제
* **문제:** MySQL 8.0 컨테이너 구동 시 `Can't create thread` 에러 발생.
* **해결:** Docker 실행 옵션에 `--security-opt seccomp=unconfined`를 추가하여 호스트 OS의 보안 정책 충돌 해결.

### 🔥 3. 레거시 Docker 네트워크 장벽
* **문제:** `Docker Toolbox` 환경 특성상 `localhost` 바인딩 실패 및 `Connection Refused` 발생.
* **해결:** 컨테이너가 가상머신 IP(`192.168.99.100`)에 바인딩됨을 파악하고, `application.properties` 설정을 변경하여 해결.

### 🔥 4. Spring Boot 3.x 파라미터 바인딩 이슈
* **문제:** Controller에서 `@RequestParam` 사용 시 `IllegalArgumentException` 발생.
* **해결:** Spring Boot 3.2+ 컴파일러 스펙 변화에 대응하여 파라미터 이름을 명시적으로 지정(`@RequestParam("name")`)하여 해결.

## 💾 DB 스키마 (Entity: Walker)
| Field | Type | Description | Note |
|---|---|---|---|
| `id` | Long | PK | Auto Increment |
| `name` | String | 사용자 이름 | 화면 표시용 |
| `username` | String | 로그인 ID | Unique |
| `password` | String | 비밀번호 | BCrypt 암호화 |
| `latitude` | Double | 위도 (Lat) | LBS 핵심 데이터 |
| `longitude` | Double | 경도 (Lon) | LBS 핵심 데이터 |
| `dogSize` | Enum | 강아지 크기 | SMALL, MEDIUM, LARGE |

## 🏃‍♂️ 실행 방법 (How to Run)

<img width="1881" height="879" alt="결과값" src="https://github.com/user-attachments/assets/0ae2a910-04ea-4108-b9d1-38324769c240" />


**1. 인프라 실행 (Docker)**
```bash
# 1) MySQL (Port: 3307)
docker run -d --name paw-radar-db -p 3307:3306 --security-opt seccomp=unconfined -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=paw_radar -e TZ=Asia/Seoul mysql:8.0

# 2) Redis (Port: 6379)
docker run -d --name paw-radar-redis -p 6379:6379 redis


**2. 애플리케이션 실행**
* 프로젝트 루트에서 `./gradlew bootRun` 실행 또는 IDE에서 `PawRadarApplication.java` 실행.
* 서버 기동 시 `DataInitializer`가 자동으로 샘플 데이터(3건)를 DB에 적재합니다.

**3. 접속 및 테스트**
* **웹 브라우저:** `http://localhost:8080` 접속
* **테스트 계정:** 아이디 `me` / 비밀번호 `1234`
* **기능 확인:** 로그인 후 내 주변 3km 이내의 추천 산책 친구 목록 확인.



---

### 📝 회고 (Retrospective)
단기간에 LBS 서비스를 구현하며 "환경에 종속되지 않는 유연한 사고"의 중요성을 배웠습니다. 특히 자동화 도구(Lombok, Docker Desktop 등)가 작동하지 않는 제약 상황에서도, 원론적인 해결 방법(수동 코드 구현, 네트워크 구조 분석)을 통해 빠르게 문제를 해결하고 MVP를 완성한 경험은 앞으로의 개발 여정에 큰 자산이 될 것입니다.
