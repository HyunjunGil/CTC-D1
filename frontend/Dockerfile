# =====================================================
# Simple Shop Frontend - Dockerfile
# =====================================================
# 이 Dockerfile은 React Frontend 애플리케이션을 위한 멀티스테이지 빌드를 정의합니다.
# 개발 환경과 프로덕션 환경을 모두 지원합니다.

# =====================================================
# 1단계: 빌드 스테이지 (Build Stage)
# =====================================================
# 이 단계에서는 React 애플리케이션을 빌드하여 정적 파일들을 생성합니다.
# Node.js 18 Alpine 이미지를 사용하여 가벼운 빌드 환경을 구성합니다.

FROM node:20-alpine AS build

# 작업 디렉토리 설정
WORKDIR /app

# package.json과 package-lock.json 복사
# 의존성 설치를 최적화하기 위해 먼저 복사합니다.
COPY package*.json ./

# 프로덕션 의존성만 설치 (devDependencies 제외)
# npm ci를 사용하여 package-lock.json 기반으로 정확한 버전 설치
RUN npm ci --only=production && npm cache clean --force

# 소스 코드 복사
COPY . .

# Vite 애플리케이션 빌드
# dist 폴더에 최적화된 정적 파일들이 생성됩니다.
RUN npm run build

# =====================================================
# 2단계: 프로덕션 스테이지 (Production Stage)
# =====================================================
# 이 단계에서는 빌드된 정적 파일들을 Nginx 서버에 배포합니다.
# Nginx Alpine 이미지를 사용하여 최종 이미지 크기를 최소화합니다.

FROM nginx:alpine AS production

# Nginx 설정 파일 복사
# React Router를 위한 SPA 설정과 CORS 헤더를 포함합니다.
COPY nginx.conf /etc/nginx/nginx.conf

# 빌드 스테이지에서 생성된 정적 파일들을 Nginx의 기본 디렉토리로 복사
COPY --from=build /app/dist /usr/share/nginx/html

# Nginx 사용자 권한 설정
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html

# 포트 80 노출
EXPOSE 80

# Nginx 서버 시작
CMD ["nginx", "-g", "daemon off;"]

# =====================================================
# 3단계: 개발 스테이지 (Development Stage)
# =====================================================
# 이 단계는 개발 환경에서 사용됩니다.
# 핫 리로드와 개발 서버를 지원합니다.

FROM node:20-alpine AS development

# 작업 디렉토리 설정
WORKDIR /app

# package.json과 package-lock.json 복사
COPY package*.json ./

# 모든 의존성 설치 (개발 의존성 포함)
RUN npm install

# 소스 코드 복사
COPY . .

# 개발 서버 포트 노출
EXPOSE 5173

# 개발 서버 시작 (핫 리로드 활성화, 모든 인터페이스에서 접근 가능)
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]

# =====================================================
# 환경 변수 및 메타데이터
# =====================================================
# 이미지에 대한 정보를 제공합니다.

LABEL maintainer="Simple Shop Development Team"
LABEL version="1.0.0"
LABEL description="Simple Shop Frontend - React 기반 쇼핑몰 프론트엔드"
LABEL stage="production"

# =====================================================
# 헬스체크 설정
# =====================================================
# 컨테이너의 상태를 확인하기 위한 헬스체크를 설정합니다.

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# =====================================================
# 보안 설정
# =====================================================
# 보안을 위한 추가 설정들을 적용합니다.

# root 사용자 대신 nginx 사용자 사용 (nginx 사용자가 존재하는 경우에만)
# USER nginx

# 불필요한 파일들 제거
# RUN rm -rf /tmp/* /var/tmp/*

# =====================================================
# 최적화 팁
# =====================================================
# 1. 멀티스테이지 빌드로 최종 이미지 크기 최소화
# 2. Alpine Linux 사용으로 보안 취약점 감소
# 3. 프로덕션 의존성만 설치로 이미지 크기 최적화
# 4. Nginx 정적 파일 서빙으로 성능 향상
# 5. 헬스체크로 컨테이너 상태 모니터링
