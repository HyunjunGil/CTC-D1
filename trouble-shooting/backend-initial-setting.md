# 🚨 Simple Shop Backend - Trouble Shooting 가이드

## 📋 개요
이 문서는 Simple Shop Backend 개발 과정에서 발생한 모든 문제와 해결 방법을 체계적으로 정리한 것입니다. 
각 문제마다 번호를 부여하여 쉽게 참조할 수 있도록 구성했습니다.

---

## 🔧 문제 해결 목록

### 1. **Gradle Wrapper JAR 파일 누락**
**문제:** `./gradlew build` 실행 시 `org.gradle.wrapper.GradleWrapperMain` 클래스를 찾을 수 없음
```
오류: 기본 클래스 org.gradle.wrapper.GradleWrapperMain을(를) 찾거나 로드할 수 없습니다.
원인: java.lang.ClassNotFoundException: org.gradle.wrapper.GradleWrapperMain
```

**원인:** `gradle/wrapper/gradle-wrapper.jar` 파일이 존재하지 않음

**해결 방법:**
```bash
# Gradle Wrapper JAR 파일 다운로드
curl -L -o gradle/wrapper/gradle-wrapper.jar https://github.com/gradle/gradle/raw/v8.4.0/gradle/wrapper/gradle-wrapper.jar

# 또는 Gradle Wrapper 초기화
gradle wrapper --gradle-version 8.4
```

**결과:** ✅ 빌드 성공

---

### 2. **build.gradle의 war 태스크 설정 오류**
**문제:** `./gradlew build` 실행 시 `Could not find method war()` 오류
```
Could not find method war() for arguments [...] on root project 'simple-shop-backend'
```

**원인:** `war` 플러그인이 선언되지 않았는데 `war` 태스크를 사용하려고 함

**해결 방법:**
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'war'  // 이 줄 추가
}
```

**결과:** ✅ 빌드 성공

---

### 3. **JAR 파일 대신 WAR 파일 생성**
**문제:** `./gradlew build` 실행 후 `simple-shop-backend-0.0.1-SNAPSHOT.war` 파일만 생성됨

**원인:** Spring Boot의 기본 동작으로 WAR 파일이 생성됨

**해결 방법:**
```bash
# JAR 파일 생성을 위해 bootJar 태스크 사용
./gradlew bootJar

# 또는 build.gradle에서 jar 태스크 활성화
jar {
    enabled = true
}
```

**결과:** ✅ `simple-shop-backend.jar` 파일 생성

---

### 4. **PostgreSQL 데이터베이스 연결 실패**
**문제:** Backend 실행 시 `FATAL: role "shop_user" does not exist` 오류
```
org.postgresql.util.PSQLException: FATAL: role "shop_user" does not exist
```

**원인:** PostgreSQL에 `shop_user` 사용자와 `simple_shop` 데이터베이스가 생성되지 않음

**해결 방법:**
```bash
# PostgreSQL에 접속
psql postgres

# 데이터베이스 생성
CREATE DATABASE simple_shop;

# 사용자 생성 및 권한 부여
CREATE USER shop_user WITH PASSWORD 'shop_password';
GRANT ALL PRIVILEGES ON DATABASE simple_shop TO shop_user;

# 종료
\q
```

**결과:** ✅ 데이터베이스 연결 성공

---

### 5. **PostgreSQL 스키마 권한 부족**
**문제:** Backend 실행 시 `ERROR: permission denied for schema public` 오류
```
ERROR: permission denied for schema public
Position: 19
```

**원인:** `shop_user`가 `public` 스키마에 테이블을 생성할 권한이 없음

**해결 방법:**
```bash
# postgres 사용자로 접속
psql postgres

# public 스키마에 대한 모든 권한 부여
GRANT ALL ON SCHEMA public TO shop_user;

# 앞으로 생성될 객체들에 대한 기본 권한 설정
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO shop_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO shop_user;

# 데이터베이스 소유권 변경
ALTER DATABASE simple_shop OWNER TO shop_user;

# 종료
\q
```

**결과:** ✅ 테이블 생성 권한 획득

---

## 🎯 **최종 해결 결과**

### ✅ **성공적으로 해결된 문제들:**
1. Gradle 빌드 시스템 구축 완료
2. Spring Boot 애플리케이션 정상 컴파일
3. PostgreSQL 데이터베이스 연결 성공
4. JPA 엔티티 테이블 자동 생성
5. REST API 서버 정상 실행

### 🚀 **Backend 동작 상태:**
- **애플리케이션 URL**: http://localhost:8080
- **API 엔드포인트**: http://localhost:8080/api
- **데이터베이스**: PostgreSQL 연결 성공
- **테이블**: `products` 테이블 자동 생성 완료
- **서버**: Tomcat 8080 포트에서 정상 실행

---

## 📚 **참고 자료**

### **Gradle 관련:**
- [Gradle Wrapper 공식 문서](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/)

### **PostgreSQL 관련:**
- [PostgreSQL 사용자 및 권한 관리](https://www.postgresql.org/docs/current/user-manag.html)
- [PostgreSQL 스키마 권한](https://www.postgresql.org/docs/current/ddl-schemas.html)

### **Spring Boot 관련:**
- [Spring Boot 데이터베이스 연결](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html)
- [Spring Boot JPA 설정](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.jpa-and-spring-data)

---

## 💡 **문제 해결 팁**

### **1. 로그 분석 순서:**
1. 에러 메시지의 정확한 원인 파악
2. 관련 설정 파일 확인
3. 시스템 상태 점검 (데이터베이스, 포트 등)
4. 단계별 해결 방법 적용

### **2. 권한 문제 해결:**
- PostgreSQL에서는 사용자 생성과 권한 부여를 명시적으로 수행해야 함
- `public` 스키마에 대한 권한이 특히 중요
- 데이터베이스 소유권 설정도 고려

### **3. 빌드 문제 해결:**
- Gradle Wrapper 파일들이 모두 존재하는지 확인
- 플러그인과 태스크 설정의 일관성 확인
- 의존성 충돌 여부 점검

---

## 🔄 **향후 예방 방법**

1. **프로젝트 초기 설정 시:**
   - Gradle Wrapper 파일들을 미리 다운로드
   - 데이터베이스 스크립트 준비
   - 권한 설정 스크립트 작성

2. **개발 환경 구축 시:**
   - Docker Compose로 일관된 환경 구성
   - 환경별 설정 파일 분리
   - 자동화된 초기화 스크립트 작성

3. **문제 발생 시:**
   - 이 문서를 참고하여 체계적 문제 해결
   - 로그 분석을 통한 정확한 원인 파악
   - 단계별 검증을 통한 해결 방법 적용

---

**마지막 업데이트:** 2025-08-26  
**작성자:** Simple Shop Development Team  
**버전:** 1.0.0
