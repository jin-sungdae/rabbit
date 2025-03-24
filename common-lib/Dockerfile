# 1️⃣ Maven 빌드 환경 사용
FROM maven:3.8.7-eclipse-temurin-17 AS builder
WORKDIR /app/common-lib

# 2️⃣ Maven 의존성 캐싱 (속도 향상)
COPY common-lib/pom.xml .
RUN mvn dependency:go-offline -B

# 3️⃣ 프로젝트 파일 복사 및 빌드
COPY common-lib/src ./src
RUN mvn clean install -DskipTests

# 4️⃣ `.m2/repository` 공유를 위해 별도 레이어 설정
FROM maven:3.8.7-eclipse-temurin-17
WORKDIR /root/.m2
COPY --from=builder /root/.m2 /root/.m2
CMD ["echo", "Common-lib is built and installed"]
