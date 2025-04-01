
# 🛒 E-commerce


이 프로젝트는 이커머스 도메인을 기반으로 한 실무 지향 백엔드 시스템입니다. TDD(Test-Driven Development) 방식을 통해 코드 품질을 지속적으로 향상하며, 대규모 서비스 환경에서도 확장 가능하도록 설계된 구조를 지향합니다.

JPA 기반 도메인 모델링, JWT 인증, OAuth 연동, Redis 캐시, Docker 환경 구성, 테스트 전략 수립 등 실제 프로덕션에서 요구되는 핵심 기능들을 포괄하며, 테스트 케이스는 test 폴더에 체계적으로 관리하고 있습니다.

---

## ✅ 프로젝트 목표

**1.	이커머스 도메인에 최적화된 JPA 설계**
  - 엔티티 간의 명확한 관계 설정과 영속성 컨텍스트 활용으로, 대규모 트랜잭션 처리에서도 일관성과 확장성을 확보합니다.
    
**2.	JWT + OAuth2 기반 권한 위임 및 역할 인증**
  - Google, Kakao, Naver 등 OAuth2 인증을 통해 사용자 정보를 위임받고, 내부적으로는 JWT를 발급하여 BUYER/SELLER 등 Role별 접근 권한을 세분화합니다.
  - 로그인 이후 관리자 또는 판매자로 Role 변경 시, 기존 세션/토큰을 무효화하고 새로운 권한이 반영된 토큰을 재발급하는 로직을 구현해, 보안성을 극대화합니다.

**3.	Redis 기반 AccessToken/RefreshToken 관리 및 인증 실패 제어**
  - RefreshToken을 Redis에 저장하고 TTL을 부여해 자동 만료 처리하며, AccessToken은 만료 또는 재발급 시 Redis 블랙리스트(Blacklist) 방식을 활용해 즉시 무효화가 가능합니다.
  - 로그인이 반복 실패되는 경우, Redis를 통해 실패 횟수 및 잠금 상태를 추적하고 Exponential Backoff 정책을 적용해 보안 침해 시도를 적극 방어합니다.
    
**4.	Nginx 기반 RateLimit 및 트래픽 제어**
  - Nginx에서 IP 혹은 JWT Claims 단위의 RateLimit을 설정해, 악성 트래픽이나 과도한 요청으로부터 서버를 보호합니다.
  -	향후 Resilience4j 등과 연계해 내부 API RateLimit도 처리 가능하도록 확장성을 고려합니다.
    
**5.	Blue-Green 기반 CI/CD 파이프라인 준비**
  -	Docker, Nginx, Spring Boot 등으로 구성된 환경에서 블루-그린 배포 전략을 사용해 무중단 배포를 지원합니다.
  -	GitHub Actions 또는 Jenkins와 연동하여 자동화 테스트 후 안정적으로 버전을 스위칭, 가용성을 높입니다.
    
**6.	K6 기반 부하(성능) 테스트**
  -	K6를 활용해 대량의 동시 요청 시나리오를 시뮬레이션하고, Latency, Throughput, Error Rate 등을 모니터링하여 시스템 병목 지점을 사전에 식별·개선합니다.
    
**7.	TDD 기반 코드 품질 관리**
  -	모든 백엔드 로직은 JUnit5를 기반으로 유닛 테스트를 작성하고, Mockito, RestAssured, TestContainers 등으로 통합 테스트까지 반복 검증하며, 안정적이고 유지보수 가능한 코드를 유지합니다.
  -	작은 단위로 테스트를 작성하고, Red-Green-Refactor 과정을 엄격하게 준수하여 기능 구현 과정에서 결함을 최소화하고 리팩토링 효율을 극대화합니다.

---

## ⚙️ 기술 스택

| 계층       | 기술                                          |
|------------|-----------------------------------------------|
| Language   | Java 17                                       |
| Framework  | Spring Boot 3.x, Spring Security, JPA         |
| Auth       | JWT, OAuth2 (Google, Kakao, Naver), PrincipalDetails |
| DB         | MySQL                                         |
| Cache      | Redis (RefreshToken 저장소 및 인증 실패 캐싱) |
| Infra      | Docker, Docker Compose, Nginx                 |
| Docs       | Swagger, Spring REST Docs                     |
| Test       | JUnit5, Mockito, RestAssured, TestContainers  |
| Monitoring | (Optional) Actuator, Prometheus               |

---

## 🗂️ 주요 기능

### ✅ 인증 및 사용자 관리
- [x] 회원가입 / 로그인 / 로그아웃 (JWT 기반)
- [x] JWT Access & Refresh Token 발급 및 재발급 (`/auth/refresh`)
- [x] Redis에 RefreshToken 저장 + TTL 기반 자동 만료
- [x] OAuth 로그인 성공 시 JWT 자동 발급 및 클라이언트 전송
- [x] 로그인 실패 시 Redis에 시도 횟수 저장 및 점진적 lock 시간 증가
- [x] 30일 이상 반복 실패 시 영구 잠금
- [x] `/me` API로 최소 정보 확인 (로그인 유지 확인용)

### ✅ 사용자 마이페이지
- [x] 내 정보 조회 및 수정
- [x] 주문 내역 조회 (Mock)
- [x] 찜한 상품 관리 (Mock)
- [x] SELLER 신청 기능 (단방향 접근 허용)
- [x] SELLER 신청 후 매장 정보 입력 가능 (Lazy 관계 기반)

### ✅ 상품 / 주문 / 장바구니 (구현 중)
- [ ] 상품 등록 / 수정 / 조회 / 삭제 (SELLER 권한)
- [ ] 장바구니 CRUD (BUYER 권한)
- [ ] 주문 생성 및 결제 연동 예정

---

## 🔒 인증 구조 요약

1. 로그인 성공 시 AccessToken (15분), RefreshToken (7일) 발급
2. RefreshToken은 Redis에 저장 (Key: userId)
3. `/auth/refresh` 호출 시 Redis에서 검증 → 새로운 AccessToken 발급
4. AccessToken 만료 시 자동 로그아웃 또는 RefreshToken으로 재발급
5. 로그인 실패 시 Redis에 실패 횟수 및 TTL 저장 → Exponential Backoff
6. 30일 초과 실패 시 영구 잠금
7. 로그아웃 시 Redis에서 RefreshToken 제거 + AccessToken 블랙리스트 등록

---

## 🐳 실행 방법

```bash
# 1. 도커 환경에서 애플리케이션 및 Redis, MySQL 실행
$ docker-compose up --build

# 2. Swagger 문서 확인
http://localhost:8080/swagger-ui/index.html
```

---

## 🧪 테스트 전략

- 단위 테스트: JUnit5 + Mockito 를 통한 도메인 및 서비스 계층 검증
- 통합 테스트: @SpringBootTest + TestContainers 기반 통합 검증
- API 테스트: RestAssured 를 통한 컨트롤러 시나리오 테스트
- 보안 테스트: 로그인 실패, 영구잠금, 인증 실패, Redis TTL 검사 등 포함
- RateLimit 테스트: K6 기반 10초 내 6회 이상 호출 시 429 반환 확인
- 프론트 테스트: 로그인 상태 유지, role 기반 라우팅 확인, `/me` 응답 기반 클라이언트 상태 초기화 검증

---

## 🧱 디렉토리 구조 (예정)

```
/src
 ├ /auth           - JWT, OAuth 인증 및 보안 흐름
 ├ /user           - 사용자 도메인 (엔티티, 서비스, 컨트롤러, DTO)
 ├ /product        - 상품 관련 도메인
 ├ /order          - 주문 관련 도메인
 ├ /store          - 판매자 상품 관리 도메인
 ├ /catalog        - 상품 조회
/config            - Spring Config, Security 설정, Filter 등록
/test              - 단위 테스트
```

---

## 📎 향후 개선 예정

- Kafka 기반 주문/결제 이벤트 아키텍처 전환
- ElasticSearch 통한 상품 검색 최적화
- Spring Cloud 기반 MSA 분리 실험
- GitHub Actions + AWS 기반 CI/CD 자동화 배포
- Grafana + Prometheus 모니터링 대시보드 구축

---

## 🙋🏻‍♂️ 만든 사람

- **진성대 (Jin Sungdae)**
- GitHub: [jin-sungdae](https://github.com/jin-sungdae)
- Blog: [velog.io/@sjin/posts](https://velog.io/@sjin/posts)

---

> 본 프로젝트는 대규모 서비스 설계와 백엔드 아키텍처 역량을 종합적으로 보여주기 위한 데모 프로젝트입니다. TDD 방식과 JPA 기반 설계를 결합하여, Redis 캐시 아키텍처, OAuth + JWT 보안 모델링 등 실무 수준의 요구사항을 모두 다루고 있습니다. 이를 통해 높은 코드 품질, 유연한 확장성, 그리고 체계적인 인증/권한 구조를 경험하실 수 있습니다.

