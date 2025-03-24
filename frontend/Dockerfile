# ===============================
# 1. Build Stage
# ===============================
FROM node:18-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 패키지 파일 복사 및 설치
COPY package.json package-lock.json ./
RUN npm install

# 애플리케이션 코드 복사
COPY . .

# Nuxt 빌드 실행
RUN npm run build


# ===============================
# 2. Production Stage
# ===============================
FROM node:18-alpine

WORKDIR /app

# 빌드된 파일 복사
COPY --from=builder /app/.output ./

# 포트 설정 (Nuxt 기본 포트: 3000)
EXPOSE 3000

# 실행 명령어 (Nuxt 앱 실행)
CMD ["node", "server/index.mjs"]