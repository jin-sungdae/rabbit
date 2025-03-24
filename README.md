
# 🛒 E-commerce Backend System (Spring Boot 기반)


이 프로젝트는 이커머스 도메인으로 설계된 면접용 백엔드 시스템입니다.

JPA 기반 도메인 모델링, JWT 인증, OAuth 연동, Redis 캐시, Docker 환경 구성, TDD 기반 개발, 부하 테스트 등 실무 수준에서 요구되는 기능과 설계를 반영하였습니다.

---

## ✅ 프로젝트 목표

- 이커머스 도메인에 최적화된 JPA 기반 백엔드 시스템 설계
- 인증과 캐시 처리, 도메인 연관관계 최적화 등 실무 적용 경험 기반 구현
- OAuth 기반 인증 연동 (구글, 카카오, 네이버)
- Redis를 통한 토큰 캐싱 및 상태 관리
- Docker 기반 로컬/테스트 환경 구성
- RestAssured + TestContainers 기반 테스트 전략 적용
---

## ⚙️ 기술 스택

| 계층       | 기술                                   |
|------------|----------------------------------------|
| Language   | Java 17                                |
| Framework  | Spring Boot, Spring Security, JPA     |
| Auth       | JWT, Spring Security OAuth2 (구글, 카카오, 네이버) |
| DB         | MySQL                                  |
| Cache      | Redis (RefreshToken 저장소 및 캐시 계층) |
| Docs       | Swagger, Spring REST Docs              |
| Infra      | Docker, Docker Compose                 |
| Test       | JUnit5, RestAssured, TestContainers    |
| Monitoring | (Optional) Actuator, Prometheus        |

---

## 🗂️ 주요 기능

- [x] 사용자 회원가입 / 로그인 / 로그아웃
- [x] JWT Access & Refresh Token 발급 및 재발급 (`/auth/refresh`)
- [ ] 소셜 로그인 (카카오, 네이버, 구글)
- [ ] 상품 등록 / 수정 / 조회 / 삭제
- [ ] 장바구니 CRUD
- [ ] 주문 생성 및 결제 연동 예정
- [ ] 관리자용 대시보드 (상품/사용자 통계)

---

## 🔒 인증 구조 요약

1. 회원 로그인 시 AccessToken + RefreshToken 발급 (JWT)
2. RefreshToken은 Redis에 저장 (TTL 관리)
3. `/auth/refresh` 요청 시 Redis에서 토큰 유효성 확인 후 새 AccessToken 발급
4. OAuth 로그인 성공 시 자체 JWT 발급 → 클라이언트 전달

> Redis를 통한 인증 구조는 성능/보안/스케일링 측면에서 유리하며, TTL 기반으로 자동 만료 처리됨.

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

- 단위 테스트: JUnit5, Mockito를 이용한 서비스/도메인 단위 검증
- 통합 테스트: TestContainers + SpringBootTest로 구성
- API 테스트: RestAssured를 이용한 컨트롤러 API 시나리오 테스트
- 부하 테스트: JMeter를 통한 성능 리포트 별도 `/performance/` 디렉토리에 제공 예정

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
 ┣ /auth         - JWT, OAuth 인증 흐름
 ┣ /domain       - product, cart, order 등 도메인 엔티티
 ┣ /global       - 공통 예외 처리, 응답 객체, 인터셉터
 ┣ /config       - Spring 설정 및 Redis 설정
 ┣ /redis        - Token 캐시 관리 레이어
 ┣ /docs         - API 문서 및 성능 비교 리포트
 ┗ /test         - 단위 및 통합 테스트
```

---

## 📎 향후 개선 예정

- 주문 생성 시 Kafka 또는 Event 기반 아키텍처 적용
- ElasticSearch 연동 통한 상품 검색 고도화
- Spring Cloud 기반의 MSA 분리 실험
- Cloud 환경 자동 배포 (GitHub Actions + AWS)

---

## 🙋🏻‍♂️ 만든 사람

- **진성대 (Jin Sungdae)**
- GitHub: [jin-sungdae](https://github.com/jin-sungdae)
- Blog: [velog.io/@sjin/posts](https://velog.io/@sjin/posts)

---

> 본 프로젝트는 실제 실무 경험을 바탕으로 기술력과 구조적 사고력을 보여주기 위한 백엔드 엔지니어링 데모 프로젝트입니다.
