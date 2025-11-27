🐶 멍멍 레이더 (Paw Radar)
위치 기반(LBS) 반려견 산책 친구 실시간 매칭 서비스

📖 프로젝트 소개
내 주변 3km 반경 내의 반려견 산책 친구를 찾아주고, 강아지 크기와 거리를 고려한 자체 알고리즘을 통해 최적의 산책 파트너를 추천해주는 MVP 프로젝트입니다. 최신 기술 스택(Spring Boot 3.4, MySQL 8.0)을 도입하고, Docker 환경에서의 이슈를 해결하며 안정적인 백엔드 시스템을 구축했습니다.

🛠 기술 스택 (Tech Stack)
Language: Java 17

Framework: Spring Boot 3.4.0

Database: MySQL 8.0 (Spatial Index 활용)

ORM: Spring Data JPA (Hibernate)

Security: Spring Security (BCrypt, Custom Auth)

Infrastructure: Docker (Docker Toolbox/VirtualBox)

Frontend: HTML5, Vanilla JS (Fetch API)

💡 핵심 기능 (Key Features)
LBS 위치 기반 검색 (Location Based Service)

MySQL의 Spatial Function(ST_Distance_Sphere)을 활용하여 애플리케이션 부하 없이 DB 레벨에서 반경 3km 검색 최적화.

매칭 추천 알고리즘 (Weighted Scoring)

단순 거리순 나열이 아닌, 가중치 기반 점수 시스템 구현.

거리 점수 + 궁합 점수(견종 크기 일치 여부)를 합산하여 정렬.

회원가입 및 로그인

BCryptPasswordEncoder를 이용한 비밀번호 단방향 암호화 저장.

Spring Security의 필터 체인을 커스터마이징하여 MVP에 맞는 인증 로직 구현.

🚀 트러블 슈팅 (Troubleshooting & Challenges)
개발 과정에서 마주친 주요 기술적 이슈와 해결 과정입니다.

1. Docker 환경과 OS 호환성 문제
문제: MySQL 8.0 컨테이너 구동 시 Can't create thread 에러 발생하며 중단.

원인: 호스트 OS와 Docker 간의 보안 정책(seccomp) 충돌.

해결: 실행 옵션에 --security-opt seccomp=unconfined를 추가하여 스레드 생성 제한 해제.

2. 레거시 Docker 환경의 네트워크 연결 이슈
문제: 컨테이너가 정상 구동되었음에도 Spring Boot에서 Connection Refused 발생.

원인: Docker Toolbox(VirtualBox 기반) 사용으로 인해 localhost가 아닌 가상머신 IP(192.168.99.100)를 사용해야 함을 파악.

해결: application.properties의 DB 호스트를 변경하여 연결 성공.

3. Spring Boot 3.x 파라미터 바인딩 이슈
문제: Controller에서 @RequestParam 사용 시 IllegalArgumentException 발생.

원인: Spring Boot 3.2+부터 컴파일 시 파라미터 이름이 보존되지 않아 자동 매핑 실패.

해결: @RequestParam("name")과 같이 명시적으로 이름을 지정하여 해결.

4. JPA와 Lombok 호환성 문제
문제: No default constructor 및 데이터 저장 시 필드 누락 현상.

원인: IDE 환경에서 Lombok 적용 실패로 생성자와 Getter가 생성되지 않음.

해결: Entity 클래스에 기본 생성자와 필드 주입 로직을 **Spring 표준 방식(Manual Code)**으로 직접 구현하여 안정성 확보.

💾 DB 스키마 (Entity)
Walker (산책자)

id (PK)

name, username, password (계정 정보)

latitude, longitude (위치 정보 - Double)

dogSize, isAvailable (매칭 조건)

🏃‍♂️ 실행 방법 (How to Run)
1. Docker DB 실행

Bash

docker run -d \
  --name paw-radar-db \
  -p 3307:3306 \
  --security-opt seccomp=unconfined \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=paw_radar \
  -e TZ=Asia/Seoul \
  mysql:8.0
2. 프로젝트 실행

PawRadarApplication.java 실행 (Spring Boot Run)

초기 데이터 자동 생성 (DataInitializer)

3. 접속

웹 브라우저: http://localhost:8080

테스트 계정: me / 1234

📝 회고 (Retrospective)
단기간에 LBS 백엔드 시스템을 구축하며 Docker 네트워크, Spring Boot 버전 이슈, JPA의 내부 동작 원리(Reflection)를 깊이 이해하게 되었습니다. 특히 자동화 도구(Lombok 등)에 의존하기보다, 문제가 생겼을 때 원론적인 방법(수동 구현)으로 우회하여 납기 내에 기능을 완성하는 문제 해결 능력을 길렀습니다.
