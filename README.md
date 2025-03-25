
# 🛒 E-commerce Backend System (Spring Boot 기반)


이 프로젝트는 이커머스 도메인을 기반으로 설계된 실무 지향 백엔드 시스템으로, 면접 및 대규모 서비스 아키텍처 대응을 목표로 구현되었습니다.

JPA 기반 도메인 모델링, JWT 인증, OAuth 연동, Redis 캐시, Docker 환경 구성, 테스트 전략 수립 등 실전 프로젝트에서 요구되는 구조를 포괄합니다.

---

## ✅ 프로젝트 목표

- 이커머스 도메인에 최적화된 JPA 기반 백엔드 시스템 설계
- 인증/권한/캐싱 처리 등 실무 수준 기능 완전 구현
- OAuth (Google, Kakao, Naver) 통합 로그인 기능
- Redis 기반 Refresh Token 캐시 + 인증 실패 제어 + Blacklist 처리
- Exponential Backoff 로그인 차단 정책
- JWT 기반 인증 구조 + 로그인 실패 횟수 제어 + 영구 잠금 전략
- Role 기반 인증 및 마이페이지 접근 제어 (BUYER/SELLER 분리)
- SELLER 전환 신청 기능 및 판매자 정보 등록 기능
- Docker 기반 CI/CD 준비 (Nuxt + Spring Boot + Nginx + Redis)
- Resilience4j 기반 Rate Limiting 및 API 보호 적용
- RestAssured + TestContainers 기반 테스트 전략 설계
  
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
- RateLimit 테스트: Resilience4j 기반 5초 내 2회 이상 호출 시 429 반환 확인
- 프론트 테스트: 로그인 상태 유지, role 기반 라우팅 확인, `/me` 응답 기반 클라이언트 상태 초기화 검증

---

## 📊 MyBatis → JPA 변경 비교 실험

| 항목 | JPA 방식 |
|------|---------------|
| 쿼리 작성 | Repository + QueryDSL / Specification |
| 복잡한 Join  | Fetch Join / Native Query 활용 |
| 유지보수성 | 높음 (타입 안정성) |
| 동적 쿼리 | Criteria, Specification |

> 실험 결과 및 성능 비교 리포트는 `/docs/orm-analysis.md`에서 확인 예정입니다.

---

## 🧱 디렉토리 구조 (예정)

```
/src
 ├ /auth           - JWT, OAuth 인증 및 보안 흐름
 ├ /user           - 사용자 도메인 (엔티티, 서비스, 컨트롤러, DTO)
 ├ /product        - 상품 관련 도메인
 ├ /order          - 주문 관련 도메인
 ├ /seller         - 판매자 신청 및 프로필 관리
 ├ /global         - 전역 응답, 예외처리, 인터셉터
 ├ /redis          - Redis 기반 리포지토리 계층
 ├ /config         - Spring Config, Security 설정, Filter 등록
 ├ /support        - 테스트 전용 어노테이션, SecurityContextFactory
 ├ /test           - RestAssured 및 단위 테스트 모듈
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

> 본 프로젝트는 대규모 서비스 설계 및 백엔드 구조적 사고를 증명하기 위한 엔지니어링 데모 프로젝트입니다. JPA 기반 설계의 강점과 Redis 캐시 아키텍처, OAuth + JWT 보안 모델링까지 실제 실무에 가까운 수준으로 설계 및 구현되었습니다.

