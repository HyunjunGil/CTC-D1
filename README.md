# 🛍️ Simple Shop - 간단한 쇼핑몰 프로젝트

## 📋 프로젝트 개요

Simple Shop은 상품 CRUD(Create, Read, Update, Delete) 기능을 제공하는 간단한 쇼핑몰 애플리케이션입니다. 
Spring Boot 백엔드와 React 프론트엔드로 구성되어 있으며, Docker Compose를 통해 개발 환경을 쉽게 구성할 수 있습니다.

## 🏗️ 프로젝트 구조

```
simple-shop/
├── backend/                    # Spring Boot 백엔드 애플리케이션
│   ├── src/main/java/com/shop/
│   │   ├── ShopApplication.java    # 메인 애플리케이션 클래스
│   │   ├── controller/             # REST API 컨트롤러
│   │   ├── entity/                 # JPA 엔티티
│   │   ├── repository/             # 데이터 접근 계층
│   │   ├── service/                # 비즈니스 로직 계층
│   │   └── config/                 # 설정 클래스들
│   ├── src/main/resources/
│   │   └── application.yml         # 애플리케이션 설정
│   ├── build.gradle               # Gradle 빌드 설정
│   └── Dockerfile                 # 백엔드 컨테이너 설정
├── frontend/                     # React 프론트엔드 애플리케이션
│   ├── src/
│   │   ├── components/            # React 컴포넌트들
│   │   ├── services/              # API 통신 서비스
│   │   ├── App.js                 # 메인 앱 컴포넌트
│   │   └── index.js               # 진입점
│   ├── package.json               # Node.js 의존성 및 스크립트
│   └── Dockerfile                 # 프론트엔드 컨테이너 설정
├── docker-compose.yml            # 전체 서비스 구성 파일
├── .gitignore                    # Git 제외 파일 설정
└── README.md                     # 프로젝트 문서 (현재 파일)
```

## 🛠️ 기술 스택

### Backend
- **Java 17** - 최신 LTS 버전의 Java
- **Spring Boot 3.x** - 엔터프라이즈급 Java 웹 프레임워크
- **Gradle** - 빌드 도구 및 의존성 관리
- **JPA/Hibernate** - 객체-관계 매핑 프레임워크
- **PostgreSQL 15** - 강력한 오픈소스 관계형 데이터베이스

### Frontend
- **React 18** - 사용자 인터페이스 구축을 위한 JavaScript 라이브러리
- **Axios** - HTTP 클라이언트 라이브러리
- **CSS3** - 스타일링

### Infrastructure
- **Docker** - 컨테이너화 플랫폼
- **Docker Compose** - 다중 컨테이너 애플리케이션 관리

## 🚀 빠른 시작

### 사전 요구사항
- Docker Desktop 설치 및 실행
- Git 설치

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd simple-shop
```

### 2. Docker Compose로 실행
```bash
# 모든 서비스 시작 (백그라운드)
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 특정 서비스 로그 확인
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f database
```

### 3. 접속 확인
- **프론트엔드**: http://localhost:3000
- **백엔드 API**: http://localhost:8080
- **데이터베이스**: localhost:5432

## 📚 API 엔드포인트

### 상품 관리 API

| 메서드 | 엔드포인트 | 설명 | 요청 본문 |
|--------|------------|------|------------|
| `GET` | `/api/products` | 전체 상품 조회 | - |
| `GET` | `/api/products/{id}` | 특정 상품 조회 | - |
| `POST` | `/api/products` | 상품 생성 | 상품 정보 (JSON) |
| `PUT` | `/api/products/{id}` | 상품 수정 | 상품 정보 (JSON) |
| `DELETE` | `/api/products/{id}` | 상품 삭제 | - |

### 상품 엔티티 구조
```json
{
  "id": 1,
  "name": "상품명",
  "description": "상품 설명",
  "price": 10000.00,
  "createdAt": "2024-01-01T00:00:00"
}
```

## 🐳 Docker 명령어 가이드

### 기본 명령어
```bash
# 서비스 시작
docker-compose up -d

# 서비스 중지
docker-compose down

# 서비스 재시작
docker-compose restart [service-name]

# 로그 확인
docker-compose logs -f [service-name]

# 상태 확인
docker-compose ps
```

### 개발 관련 명령어
```bash
# 이미지 재빌드 후 시작
docker-compose up --build

# 특정 서비스만 재빌드
docker-compose build [service-name]

# 볼륨 삭제 (데이터 초기화)
docker-compose down -v

# 모든 컨테이너, 이미지, 볼륨 삭제
docker-compose down --rmi all --volumes
```

### 디버깅 명령어
```bash
# 컨테이너 내부 접속
docker-compose exec [service-name] /bin/bash

# 백엔드 컨테이너 접속
docker-compose exec backend /bin/bash

# 프론트엔드 컨테이너 접속
docker-compose exec frontend /bin/bash

# 데이터베이스 접속
docker-compose exec database psql -U shop_user -d simple_shop
```

## 🔧 개발 환경 설정

### 개별 실행 (Docker 없이)

#### Backend 실행
```bash
cd backend
./gradlew bootRun
```

#### Frontend 실행
```bash
cd frontend
npm install
npm start
```

#### Database 설정
- PostgreSQL 15 설치
- 데이터베이스 `simple_shop` 생성
- 사용자 `shop_user` 생성 및 권한 부여
- `application.yml`에서 연결 정보 설정

## 📁 주요 파일 설명

### Backend
- **`ShopApplication.java`**: Spring Boot 메인 클래스
- **`ProductController.java`**: REST API 엔드포인트 정의
- **`Product.java`**: 상품 엔티티 클래스
- **`ProductRepository.java`**: 데이터 접근 인터페이스
- **`ProductService.java`**: 비즈니스 로직 구현
- **`CorsConfig.java`**: CORS 정책 설정

### Frontend
- **`ProductList.js`**: 상품 목록 표시 및 삭제 기능
- **`ProductForm.js`**: 상품 추가/수정 폼
- **`App.js`**: 메인 애플리케이션 컴포넌트
- **`api.js`**: 백엔드 API 통신 서비스

## 🚨 문제 해결

### 일반적인 문제들

#### 포트 충돌
```bash
# 사용 중인 포트 확인
lsof -i :3000
lsof -i :8080
lsof -i :5432

# docker-compose.yml에서 포트 변경
ports:
  - "3001:3000"  # 호스트 포트를 3001로 변경
```

#### 데이터베이스 연결 실패
```bash
# 데이터베이스 서비스 상태 확인
docker-compose ps database

# 데이터베이스 로그 확인
docker-compose logs database

# 데이터베이스 서비스 재시작
docker-compose restart database
```

#### 빌드 실패
```bash
# 캐시 삭제 후 재빌드
docker-compose build --no-cache

# 특정 서비스만 재빌드
docker-compose build --no-cache backend
```

## 📝 개발 가이드라인

### Backend 개발
- Java 17 문법 사용
- Spring Boot 3.x 의존성 사용
- JPA Repository 패턴 활용
- 예외 처리 포함
- CORS 설정으로 프론트엔드 연동

### Frontend 개발
- 함수형 컴포넌트 + Hooks 사용
- Axios로 API 통신
- 에러 처리 포함
- 상품 추가 후 목록 자동 갱신

### Docker 관련
- 멀티스테이지 빌드 사용
- 개발용 볼륨 마운트 설정
- Health check로 서비스 의존성 관리

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 문의

프로젝트에 대한 질문이나 제안사항이 있으시면 이슈를 생성해 주세요.

---

**Happy Coding! 🎉**