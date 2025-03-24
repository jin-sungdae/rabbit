# Stage 1: 공통 모듈 빌드
FROM maven:3.8.7-eclipse-temurin-17 AS common-builder
WORKDIR /app/common

COPY common-lib/pom.xml .
COPY common-lib/src ./src
RUN mvn clean install -DskipTests

# Stage 2: User Server 빌드 (JAR 파일 생성)
FROM maven:3.8.7-eclipse-temurin-17 AS user-builder
WORKDIR /app/user

# 공통 모듈 캐싱
COPY --from=common-builder /root/.m2 /root/.m2

# 프로젝트 소스 복사 및 빌드
COPY user-server/pom.xml .
COPY user-server/src ./src

RUN mvn clean package -DskipTests

# Stage 3: 실행 컨테이너
FROM openjdk:17-jdk-slim
WORKDIR /app
# 빌드된 JAR 복사
COPY --from=user-builder /app/user/target/user-server-0.0.1-SNAPSHOT.jar app.jar

# 실행 명령
CMD ["java", "-jar", "app.jar"]
