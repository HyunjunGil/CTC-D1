# 프론트엔드 Docker Compose + Nginx 설정 문제 해결 가이드

## 문제 상황
프론트엔드 컨테이너가 계속 재시작되며 Nginx 설정 오류로 인해 실행되지 않는 문제가 발생했습니다.

## 에러 메시지
```
nginx: [emerg] "add_header" directive is not allowed here in /etc/nginx/nginx.conf:108
```

## 문제 원인 분석

### 1. Docker Compose 설정 문제
- **포트 불일치**: `docker-compose.yml`에서 `"5173:5173"` (Vite 개발 서버 포트)로 설정
- **스테이지 미지정**: `target: production`을 지정하지 않아 기본 스테이지 사용
- **환경 설정 불일치**: 개발용 볼륨 마운트와 프로덕션 환경이 혼재

### 2. Nginx 설정 파일 문제
- **`add_header` 위치 오류**: `http` 블록에서 직접 사용 (허용되지 않음)
- **`if` 블록 내 `add_header`**: Nginx에서 지원하지 않는 구문
- **CORS 헤더 설정 오류**: 잘못된 위치에 설정

### 3. Dockerfile 멀티스테이지 빌드 문제
- **빌드 도구 누락**: `npm ci --only=production`으로 빌드 도구 설치 안됨
- **의존성 문제**: `vite`, `@vitejs/plugin-react` 등 `devDependencies` 누락

## 해결 과정

### 1단계: Docker Compose 설정 수정

#### 포트 설정 수정
```yaml
# 수정 전 (잘못된 설정)
ports:
  - "5173:5173"  # Vite 개발 서버 포트

# 수정 후 (올바른 설정)
ports:
  - "3000:80"    # Nginx 포트로 매핑
```

#### 프로덕션 스테이지 지정
```yaml
build:
  context: ./frontend
  dockerfile: Dockerfile
  target: production  # 프로덕션 스테이지 명시적 지정
```

### 2단계: Dockerfile 빌드 문제 해결

#### 의존성 설치 수정
```dockerfile
# 수정 전 (빌드 도구 누락)
RUN npm ci --only=production && npm cache clean --force

# 수정 후 (모든 의존성 설치)
RUN npm install
```

**설명**: `--only=production`은 런타임 의존성만 설치하므로 빌드에 필요한 `vite`, `@vitejs/plugin-react` 등이 누락됩니다.

### 3단계: Nginx 설정 파일 수정

#### 잘못된 `add_header` 제거
```nginx
# 수정 전 (http 블록에서 직접 사용 - 허용되지 않음)
http {
    # ... 기타 설정 ...
    
    # 클릭재킹 방지
    add_header X-Frame-Options "SAMEORIGIN" always;
    
    # XSS 방지
    add_header X-XSS-Protection "1; mode=block" always;
    
    # MIME 타입 스니핑 방지
    add_header X-Content-Type-Options "nosniff" always;
    
    # 참조 정책
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    
    server {
        # ... 서버 설정 ...
    }
}

# 수정 후 (http 블록에서 제거)
http {
    # ... 기타 설정 ...
    
    server {
        # ... 서버 설정 ...
        
        # 보안 헤더는 server 블록 내부에 설정
        location / {
            add_header X-Frame-Options "SAMEORIGIN" always;
            add_header X-XSS-Protection "1; mode=block" always;
            add_header X-Content-Type-Options "nosniff" always;
        }
    }
}
```

#### `if` 블록 내 `add_header` 제거
```nginx
# 수정 전 (if 블록 내 add_header - 허용되지 않음)
if ($request_method = 'OPTIONS') {
    add_header 'Access-Control-Allow-Origin' '*';
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS';
    add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization';
    add_header 'Access-Control-Max-Age' 1728000;
    add_header 'Content-Type' 'text/plain; charset=utf-8';
    add_header 'Content-Length' 0;
    return 204;
}

# 수정 후 (단순화)
if ($request_method = 'OPTIONS') {
    return 204;
}
```

## 최종 설정

### docker-compose.yml
```yaml
frontend:
  build:
    context: ./frontend
    dockerfile: Dockerfile
    target: production  # 프로덕션 스테이지 명시적 지정
  
  ports:
    - "3000:80"        # 호스트:3000 → 컨테이너:80 (Nginx)
  
  depends_on:
    backend:
      condition: service_healthy
```

### Dockerfile (프론트엔드)
```dockerfile
# 1단계: 빌드 스테이지
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install  # 모든 의존성 설치 (빌드 도구 포함)
COPY . .
RUN npm run build

# 2단계: 프로덕션 스테이지
FROM nginx:alpine AS production
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### nginx.conf
```nginx
http {
    # ... 기본 설정 ...
    
    server {
        listen 80;
        server_name localhost;
        root /usr/share/nginx/html;
        index index.html index.htm;
        
        # CORS 설정 (단순화)
        if ($request_method = 'OPTIONS') {
            return 204;
        }
        
        # SPA 라우팅
        location / {
            try_files $uri $uri/ /index.html;
            
            # 보안 헤더 (server 블록 내부에 올바르게 설정)
            add_header X-Frame-Options "SAMEORIGIN" always;
            add_header X-XSS-Protection "1; mode=block" always;
            add_header X-Content-Type-Options "nosniff" always;
        }
        
        # ... 기타 설정 ...
    }
}
```

## 실행 순서

### 1. 컨테이너 중지 및 정리
```bash
docker-compose down
docker rmi ctc-d1-frontend
docker builder prune -f
```

### 2. 재빌드 및 실행
```bash
docker-compose up --build -d
```

### 3. 상태 확인
```bash
docker ps -a | grep frontend
docker logs simple-shop-frontend
```

## 핵심 포인트

### 1. 멀티스테이지 빌드 이해
- **`build` 스테이지**: React 앱 빌드 (최종 이미지에 포함되지 않음)
- **`production` 스테이지**: Nginx로 정적 파일 서빙 (최종 이미지에 포함)

### 2. Nginx 설정 규칙
- **`add_header`**: `http`, `server`, `location` 블록에서만 사용 가능
- **`if` 블록**: 제한적인 기능, `add_header` 사용 불가

### 3. Docker Compose 설정
- **`target` 지정**: 원하는 스테이지 명시적 선택
- **포트 매핑**: 컨테이너 내부 포트와 호스트 포트 일치 필요

## 접근 URL
- **프론트엔드**: `http://localhost:3000`
- **백엔드**: `http://localhost:8080`
- **데이터베이스**: `localhost:5432`

## 문제 해결 완료
이제 프론트엔드 컨테이너가 정상적으로 실행되며, Nginx를 통해 React 앱이 정적 파일로 서빙됩니다.
