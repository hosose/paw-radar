# 🐶 멍멍 레이더 (Paw Radar)
> **위치 기반(LBS) 반려견 산책 친구 실시간 매칭 서비스**

## 📖 프로젝트 소개
**"우리 강아지와 딱 맞는 산책 친구를 찾을 수 없을까?"** 라는 고민에서 시작된 MVP 프로젝트입니다.
사용자의 현재 위치(위도/경도)를 기반으로 반경 3km 이내의 산책 친구를 탐색하고, 거리와 견종 크기(대/중/소)를 고려한 **가중치 매칭 알고리즘**을 통해 최적의 파트너를 추천합니다.

레거시 환경(Docker Toolbox)과 최신 기술 스택(Spring Boot 3.4)을 통합하는 과정에서 발생한 다양한 인프라 및 프레임워크 이슈를 해결하며 안정적인 백엔드 시스템을 구축했습니다.

## 🛠 기술 스택 (Tech Stack)
### Backend
* **Language:** Java 17
* **Framework:** Spring Boot 3.4.0
* **ORM:** Spring Data JPA (Hibernate 6.x)
* **Security:** Spring Security (BCrypt Encryption)
* **Build Tool:** Gradle

### Infrastructure & Database
* **Database:** MySQL 8.0 (Spatial Index 적용)
* **Infrastructure:** Docker, Docker Toolbox (VirtualBox Environment)

### Frontend (MVP)
* **Tech:** HTML5, CSS3, Vanilla JavaScript (Fetch API)

## 💡 핵심 기능 (Key Features)
1.  **LBS 위치 기반 검색 (Location Based Service)**
    * MySQL 8.0의 공간 함수(`ST_Distance_Sphere`)를 활용하여, 애플리케이션 레벨의 연산 부하 없이 DB단에서 반경 3km 이내 사용자를 고속으로 필터링.
2.  **가중치 기반 매칭 알고리즘 (Weighted Scoring)**
    * 단순 거리순 정렬을 넘어선 사용자 경험(UX) 중심 알고리즘 구현.
    * **거리 점수** (가까울수록 고득점) + **궁합 점수** (견종 크기가 같으면 가산점)를 합산하여 추천 우선순위 산정.
3.  **사용자 인증 시스템**
    * Spring Security Filter Chain을 커스터마이징하여 API 서버에 적합한 보안 설정 적용.
    * `BCryptPasswordEncoder`를 이용한 비밀번호 단방향 암호화 저장 및 검증.

## 🚀 트러블 슈팅 (Troubleshooting & Challenges)
> **개발 과정에서 마주친 주요 기술적 난관과 이를 해결한 과정입니다.**

### 1. Docker 컨테이너와 Host OS 호환성 문제
* **문제:** MySQL 8.0 컨테이너 실행 시 `Can't create thread` 에러 발생하며 즉시 중단됨.
* **분석:** Host OS의 보안 정책(seccomp)과 최신 MySQL 이미지 간의 충돌 확인.
* **해결:** Docker 실행 옵션에 `--security-opt seccomp=unconfined`를 추가하여 스레드 생성 제한을 해제함.

### 2. 레거시 Docker 환경(Toolbox)의 네트워크 이슈
* **문제:** 컨테이너가 정상 구동되었음에도 Spring Boot에서 `Connection Refused` 발생.
* **분석:** 최신 Docker Desktop(localhost)이 아닌 Docker Toolbox(VirtualBox) 환경임을 인지. 컨테이너가 로컬 호스트가 아닌 별도의 가상머신 IP(`192.168.99.100`)에 바인딩됨을 확인.
* **해결:** `application.properties`의 DB 호스트 주소를 로컬(`127.0.0.1`)에서 가상머신 IP(`192.168.99.100`)로 변경하여 연결 성공.

### 3. Spring Boot 3.x의 파라미터 바인딩 변화
* **문제:** Controller에서 `@RequestParam` 사용 시 `IllegalArgumentException: Name for argument... not specified` 에러 발생.
* **분석:** Spring Boot 3.2부터 컴파일 시 파라미터 이름이 바이트코드에 유지되지 않아( `-parameters` 플래그 부재) 자동 매핑이 실패하는 현상 파악.
* **해결:** `@RequestParam("username")`과 같이 어노테이션에 파라미터 이름을 명시적으로 지정하여 해결.

### 4. JPA 엔티티와 Lombok 호환성 문제
* **문제:** `No default constructor` 에러 및 데이터 저장 시 특정 필드(`name`) 누락 현상 발생.
* **분석:** IDE 환경 설정 문제로 Lombok 어노테이션이 적용되지 않아 기본 생성자와 Getter가 생성되지 않음.
* **해결:** `@Builder` 패턴에 의존하기보다, 엔티티 클래스에 기본 생성자와 필드 주입 로직을 **Spring 표준 방식(Manual Code)**으로 직접 구현하여 안정성을 확보함.

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
| `isAvailable`| Boolean | 산책 가능 여부 | 매칭 필터링 조건 |

## 🏃‍♂️ 실행 방법 (How to Run)

**1. 데이터베이스 실행 (Docker)**
\`\`\`bash
# 기존 컨테이너 정리 후 실행
docker rm -f paw-radar-db

docker run -d \
  --name paw-radar-db \
  -p 3307:3306 \
  --security-opt seccomp=unconfined \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=paw_radar \
  -e TZ=Asia/Seoul \
  mysql:8.0
\`\`\`

**2. 애플리케이션 실행**
* 프로젝트 루트에서 `./gradlew bootRun` 실행 또는 IDE에서 `PawRadarApplication.java` 실행.
* 서버 기동 시 `DataInitializer`가 자동으로 샘플 데이터(3건)를 DB에 적재합니다.

**3. 접속 및 테스트**
* **웹 브라우저:** `http://localhost:8080` 접속
* **테스트 계정:** 아이디 `me` / 비밀번호 `1234`
* **기능 확인:** 로그인 후 내 주변 3km 이내의 추천 산책 친구 목록 확인.
  <img width="1916" height="953" alt="결과값" src="https://github.com/user-attachments/assets/1dd0f37f-65e8-42b7-9f55-e7e9fa98b965" />


---

### 📝 회고 (Retrospective)
단기간에 LBS 서비스를 구현하며 "환경에 종속되지 않는 유연한 사고"의 중요성을 배웠습니다. 특히 자동화 도구(Lombok, Docker Desktop 등)가 작동하지 않는 제약 상황에서도, 원론적인 해결 방법(수동 코드 구현, 네트워크 구조 분석)을 통해 빠르게 문제를 해결하고 MVP를 완성한 경험은 앞으로의 개발 여정에 큰 자산이 될 것입니다.
