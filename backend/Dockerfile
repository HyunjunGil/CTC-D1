# =====================================================
# Simple Shop Backend - Dockerfile
# =====================================================
# 이 Dockerfile은 Spring Boot 애플리케이션을 Docker 컨테이너로 실행하기 위한 설정입니다.
# 멀티스테이지 빌드를 사용하여 최종 이미지 크기를 최소화하고 보안을 강화합니다.

# =====================================================
# 1단계: 빌드 스테이지 (Build Stage)
# =====================================================
# 이 단계에서는 소스 코드를 컴파일하고 JAR 파일을 생성합니다.
# Gradle을 사용하여 의존성을 다운로드하고 애플리케이션을 빌드합니다.

# 사용할 베이스 이미지를 지정합니다.
# OpenJDK 17을 사용하여 Java 17 애플리케이션을 실행할 수 있습니다.
# alpine 리눅스는 가벼운 리눅스 배포판으로 이미지 크기를 줄여줍니다.
FROM eclipse-temurin:17-jdk AS build

# 메타데이터를 설정합니다 (선택사항이지만 권장).
LABEL maintainer="Simple Shop Team"
LABEL description="Simple Shop Backend Application"
LABEL version="1.0.0"

# 작업 디렉토리를 설정합니다.
# 이 디렉토리에서 모든 작업이 수행됩니다.
WORKDIR /app

# 시스템 패키지를 업데이트하고 필요한 도구들을 설치합니다.
# --no-cache 옵션을 사용하여 패키지 캐시를 저장하지 않아 이미지 크기를 줄입니다.
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    wget \
    unzip \
    bash \
    && rm -rf /var/lib/apt/lists/*

# Gradle Wrapper를 다운로드합니다.
# Gradle Wrapper는 프로젝트에 포함된 Gradle 버전을 사용할 수 있게 해줍니다.
# 이렇게 하면 시스템에 설치된 Gradle 버전과 상관없이 일관된 빌드가 가능합니다.
COPY gradlew .
COPY gradle gradle

# Gradle Wrapper에 실행 권한을 부여합니다.
# chmod +x는 파일을 실행 가능하게 만드는 명령어입니다.
RUN chmod +x ./gradlew

# build.gradle과 settings.gradle 파일을 복사합니다.
# 이 파일들은 프로젝트의 의존성과 설정을 정의합니다.
COPY build.gradle .
COPY settings.gradle .

# Gradle 의존성을 다운로드합니다.
# --no-daemon 옵션을 사용하여 Gradle 데몬을 실행하지 않습니다.
# --stacktrace 옵션을 사용하여 오류 발생 시 상세한 스택 트레이스를 출력합니다.
# 이 단계는 Docker 레이어 캐싱을 활용하여 의존성이 변경되지 않으면 재실행되지 않습니다.
RUN ./gradlew dependencies --no-daemon --stacktrace

# 소스 코드를 복사합니다.
# src 디렉토리에는 Java 소스 코드와 리소스 파일들이 포함되어 있습니다.
COPY src src

# 애플리케이션을 빌드합니다.
# bootJar 태스크를 실행하여 실행 가능한 JAR 파일을 생성합니다.
# --no-daemon 옵션을 사용하여 Gradle 데몬을 실행하지 않습니다.
# --stacktrace 옵션을 사용하여 오류 발생 시 상세한 스택 트레이스를 출력합니다.
RUN ./gradlew bootJar --no-daemon --stacktrace

# =====================================================
# 2단계: 실행 스테이지 (Runtime Stage)
# =====================================================
# 이 단계에서는 빌드된 JAR 파일만을 포함한 최종 실행 이미지를 생성합니다.
# JRE(Java Runtime Environment)만 포함하여 이미지 크기를 최소화합니다.

# 사용할 베이스 이미지를 지정합니다.
# OpenJDK 17 JRE를 사용하여 Java 애플리케이션을 실행합니다.
# JRE는 JDK보다 작으며, 컴파일 도구가 포함되지 않아 보안상 안전합니다.
FROM eclipse-temurin:17-jre AS runtime

# 메타데이터를 설정합니다.
LABEL maintainer="Simple Shop Team"
LABEL description="Simple Shop Backend Runtime"
LABEL version="1.0.0"

# 보안을 강화하기 위한 설정을 추가합니다.
# 새로운 사용자를 생성하여 root 권한으로 애플리케이션을 실행하지 않도록 합니다.
# 이는 컨테이너 보안의 중요한 부분입니다.
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 애플리케이션 JAR 파일을 빌드 스테이지에서 복사합니다.
# --from=build 옵션을 사용하여 이전 스테이지의 파일을 가져옵니다.
COPY --from=build /app/build/libs/*.jar app.jar

# 파일 소유권을 appuser로 변경합니다.
# 이는 보안을 강화하기 위한 설정입니다.
RUN chown appuser:appgroup app.jar

# 애플리케이션 사용자로 전환합니다.
# root 권한으로 실행하는 것을 방지하여 보안을 강화합니다.
USER appuser

# 애플리케이션이 사용할 포트를 노출합니다.
# EXPOSE는 컨테이너가 사용할 포트를 문서화하는 역할을 합니다.
# 실제 포트 바인딩은 docker run 명령어나 docker-compose에서 설정합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 실행할 명령어를 설정합니다.
# java -jar 명령어로 Spring Boot 애플리케이션을 실행합니다.
# -Djava.security.egd=file:/dev/./urandom 옵션은 JVM의 엔트로피 소스를 설정하여
# 애플리케이션 시작 속도를 향상시킵니다.
# -XX:+UseContainerSupport 옵션은 컨테이너 환경에서 JVM이 메모리 제한을 인식하도록 합니다.
# -XX:MaxRAMPercentage=75.0 옵션은 컨테이너 메모리의 75%를 JVM 힙 메모리로 사용하도록 합니다.
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", \
    "app.jar"]

# =====================================================
# 3단계: 개발용 스테이지 (Development Stage) - 선택사항
# =====================================================
# 이 단계는 개발 환경에서 사용할 수 있는 추가 도구가 포함된 이미지입니다.
# 디버깅이나 모니터링이 필요한 경우에만 사용합니다.

FROM eclipse-temurin:17-jdk AS development

# 메타데이터를 설정합니다.
LABEL maintainer="Simple Shop Team"
LABEL description="Simple Shop Backend Development Environment"
LABEL version="1.0.0"

    # 개발 도구들을 설치합니다.
    RUN apt-get update && \
        apt-get install -y --no-install-recommends \
        curl \
        wget \
        vim \
        htop \
        procps \
        && rm -rf /var/lib/apt/lists/*

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 애플리케이션 JAR 파일을 복사합니다.
COPY --from=build /app/build/libs/*.jar app.jar

# 개발용 스크립트를 복사합니다 (있는 경우).
# COPY scripts/ scripts/

# 개발용 포트를 노출합니다.
# Spring Boot의 기본 포트와 디버그 포트를 노출합니다.
EXPOSE 8080 5005

# 개발 모드로 애플리케이션을 실행합니다.
# -agentlib:jdwp 옵션을 사용하여 원격 디버깅을 활성화합니다.
# suspend=n 옵션은 디버거가 연결될 때까지 기다리지 않고 애플리케이션을 시작합니다.
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", \
    "-jar", \
    "app.jar"]

# =====================================================
# 빌드 최적화 팁
# =====================================================
# 
# 1. 멀티스테이지 빌드 사용:
#    - 빌드 도구와 소스 코드는 최종 이미지에 포함되지 않음
#    - 최종 이미지 크기 최소화
#    - 보안 강화 (불필요한 도구 제거)
#
# 2. 레이어 캐싱 활용:
#    - 의존성 파일을 먼저 복사하여 캐시 활용
#    - 소스 코드 변경 시에만 재컴파일
#
# 3. 보안 강화:
#    - root 사용자 대신 일반 사용자 사용
#    - 필요한 패키지만 설치
#    - 최신 보안 패치 적용
#
# 4. 성능 최적화:
#    - JVM 옵션으로 컨테이너 환경 최적화
#    - 메모리 사용량 제한
#    - 시작 시간 단축
#
# =====================================================
# 사용법
# =====================================================
#
# 1. 프로덕션 이미지 빌드:
#    docker build -t simple-shop-backend:latest .
#
# 2. 개발용 이미지 빌드:
#    docker build --target development -t simple-shop-backend:dev .
#
# 3. 이미지 실행:
#    docker run -p 8080:8080 simple-shop-backend:latest
#
# 4. 개발용 실행 (디버깅 포함):
#    docker run -p 8080:8080 -p 5005:5005 simple-shop-backend:dev
#
# =====================================================
