# 🗄️ Simple Shop Backend - 데이터베이스 설정 가이드

## 📋 개요
이 문서는 Simple Shop Backend의 PostgreSQL 데이터베이스 설정을 자동화하는 스크립트들의 사용법을 설명합니다.

## 🚀 **자동화 스크립트들**

### 1. **init-db.sql** - PostgreSQL 초기화 SQL 스크립트
**용도:** 데이터베이스, 사용자, 권한을 설정하는 SQL 명령어 모음

**실행 방법:**
```bash
# PostgreSQL에 root 권한으로 접속하여 실행
psql -U postgres -f init-db.sql
```

**자동으로 수행하는 작업:**
- ✅ `simple_shop` 데이터베이스 생성
- ✅ `shop_user` 사용자 생성
- ✅ 데이터베이스 권한 설정
- ✅ 스키마 권한 설정
- ✅ 기본 권한 설정

---

### 2. **setup-database.sh** - Linux/macOS 자동화 스크립트
**용도:** Linux/macOS 환경에서 데이터베이스 설정을 완전 자동화

**사용법:**
```bash
# 스크립트에 실행 권한 부여
chmod +x setup-database.sh

# 스크립트 실행
./setup-database.sh
```

**자동으로 수행하는 작업:**
- ✅ PostgreSQL 서비스 상태 확인
- ✅ 데이터베이스 초기화 스크립트 실행
- ✅ 연결 테스트
- ✅ 권한 테스트
- ✅ 환경 변수 파일 생성

---

### 3. **setup-database.bat** - Windows 자동화 스크립트
**용도:** Windows 환경에서 데이터베이스 설정을 완전 자동화

**사용법:**
```bash
# 배치 파일 실행 (더블클릭 또는 명령어)
setup-database.bat

# 또는 명령 프롬프트에서
.\setup-database.bat
```

**자동으로 수행하는 작업:**
- ✅ PostgreSQL 서비스 상태 확인
- ✅ 데이터베이스 초기화 스크립트 실행
- ✅ 연결 테스트
- ✅ 권한 테스트
- ✅ 환경 변수 파일 생성

---

## 🔧 **수동 설정 방법 (스크립트 사용 불가 시)**

### **1단계: PostgreSQL 서비스 시작**
```bash
# macOS
brew services start postgresql@15

# Linux
sudo systemctl start postgresql

# Windows
net start postgresql
```

### **2단계: PostgreSQL에 접속**
```bash
psql postgres
```

### **3단계: 사용자 생성**
```sql
CREATE USER shop_user WITH PASSWORD 'shop_password';
```

### **4단계: 데이터베이스 생성**
```sql
CREATE DATABASE simple_shop OWNER shop_user;
```

### **5단계: 권한 설정**
```sql
-- simple_shop 데이터베이스에 연결
\c simple_shop

-- public 스키마 권한 부여
GRANT ALL ON SCHEMA public TO shop_user;

-- 기본 권한 설정
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO shop_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO shop_user;
```

### **6단계: 종료**
```sql
\q
```

---

## 🧪 **설정 확인 방법**

### **1. 연결 테스트**
```bash
psql -U shop_user -d simple_shop -h localhost
# 비밀번호 입력: shop_password
```

### **2. 권한 확인**
```sql
-- 현재 사용자 확인
SELECT current_user;

-- 데이터베이스 목록 확인
\l

-- 테이블 생성 테스트
CREATE TABLE test_table (id INT);
DROP TABLE test_table;
```

---

## 🚨 **문제 해결**

### **문제 1: PostgreSQL 서비스가 실행되지 않음**
```bash
# macOS
brew services start postgresql@15

# Linux
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Windows
# 서비스 관리자에서 PostgreSQL 서비스 시작
```

### **문제 2: 사용자 인증 실패**
```bash
# pg_hba.conf 파일에서 인증 방식 확인
# 또는 PostgreSQL 서비스 재시작
```

### **문제 3: 권한 부족**
```sql
-- postgres 사용자로 접속하여 권한 재설정
GRANT ALL ON SCHEMA public TO shop_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO shop_user;
```

---

## 📁 **생성되는 파일들**

### **1. .env 파일**
```
# Simple Shop Database 환경 변수
DB_NAME=simple_shop
DB_USER=shop_user
DB_PASSWORD=shop_password
DB_URL=jdbc:postgresql://localhost:5432/simple_shop
```

### **2. 데이터베이스 객체들**
- `simple_shop` 데이터베이스
- `shop_user` 사용자
- `public` 스키마 권한
- 기본 테이블/시퀀스 권한

---

## 🎯 **다음 단계**

데이터베이스 설정이 완료되면:

1. **Backend 애플리케이션 실행:**
   ```bash
   ./gradlew bootRun
   ```

2. **API 테스트:**
   ```bash
   curl http://localhost:8080/api/products
   ```

3. **데이터베이스 테이블 확인:**
   ```sql
   psql -U shop_user -d simple_shop
   \dt
   ```

---

## 📚 **참고 자료**

- [PostgreSQL 공식 문서](https://www.postgresql.org/docs/)
- [Spring Boot 데이터베이스 설정](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html)
- [JPA/Hibernate 설정](https://hibernate.org/orm/documentation/)

---

**마지막 업데이트:** 2025-08-26  
**작성자:** Simple Shop Development Team  
**버전:** 1.0.0
