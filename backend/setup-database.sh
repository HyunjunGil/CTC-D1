#!/bin/bash

# =====================================================
# Simple Shop Backend - PostgreSQL 데이터베이스 설정 스크립트
# =====================================================
# 이 스크립트는 PostgreSQL 데이터베이스를 자동으로 설정합니다.
# 사용자, 데이터베이스, 권한을 모두 자동으로 생성합니다.

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로그 함수들
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 스크립트 시작
echo "=========================================="
echo "🚀 Simple Shop Database 설정 시작"
echo "=========================================="

# 기존 PostgreSQL 서비스 종료
brew services stop postgresql@15

# PostgreSQL 서비스 상태 확인
log_info "PostgreSQL 서비스 상태를 확인합니다..."

if ! pg_isready -q; then
    log_error "PostgreSQL 서비스가 실행되지 않았습니다."
    brew services start postgresql@15
fi

log_success "PostgreSQL 서비스가 정상적으로 실행 중입니다."

# PostgreSQL 버전 확인
log_info "PostgreSQL 버전을 확인합니다..."
psql --version

# 데이터베이스 설정
DB_NAME="simple_shop"
DB_USER="shop_user"
DB_PASSWORD="shop_password"

log_info "데이터베이스 설정 정보:"
echo "  데이터베이스: $DB_NAME"
echo "  사용자: $DB_USER"
echo "  비밀번호: $DB_PASSWORD"

# 초기화 SQL 스크립트 실행
log_info "데이터베이스 초기화를 시작합니다..."

# PostgreSQL에 root 권한으로 접속하여 초기화 스크립트 실행
if psql -U postgres -f init-db.sql; then
    log_success "데이터베이스 초기화가 완료되었습니다."
else
    log_error "데이터베이스 초기화에 실패했습니다."
    log_info "수동으로 설정을 진행합니다..."
    
    # 수동 설정
    log_info "사용자를 생성합니다..."
    psql -U postgres -c "CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';" 2>/dev/null || log_warning "사용자가 이미 존재합니다."
    
    log_info "데이터베이스를 생성합니다..."
    psql -U postgres -c "CREATE DATABASE $DB_NAME OWNER $DB_USER;" 2>/dev/null || log_warning "데이터베이스가 이미 존재합니다."
    
    log_info "권한을 설정합니다..."
    psql -U postgres -d $DB_NAME -c "GRANT ALL ON SCHEMA public TO $DB_USER;" 2>/dev/null
    psql -U postgres -d $DB_NAME -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $DB_USER;" 2>/dev/null
    psql -U postgres -d $DB_NAME -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $DB_USER;" 2>/dev/null
    
    log_success "수동 설정이 완료되었습니다."
fi

# 연결 테스트
log_info "데이터베이스 연결을 테스트합니다..."

if psql -U $DB_USER -d $DB_NAME -h localhost -c "SELECT version();" >/dev/null 2>&1; then
    log_success "데이터베이스 연결 테스트가 성공했습니다."
else
    log_error "데이터베이스 연결 테스트에 실패했습니다."
    log_info "권한 설정을 다시 확인해주세요."
    exit 1
fi

# 테이블 생성 테스트
log_info "테이블 생성 권한을 테스트합니다..."

if psql -U $DB_USER -d $DB_NAME -h localhost -c "CREATE TABLE test_permission (id INT); DROP TABLE test_permission;" >/dev/null 2>&1; then
    log_success "테이블 생성 권한 테스트가 성공했습니다."
else
    log_error "테이블 생성 권한 테스트에 실패했습니다."
    log_info "스키마 권한을 다시 설정해주세요."
    exit 1
fi

# 설정 완료
echo ""
echo "=========================================="
echo "🎉 Simple Shop Database 설정 완료!"
echo "=========================================="
echo "📊 데이터베이스: $DB_NAME"
echo "👤 사용자: $DB_USER"
echo "🔑 비밀번호: $DB_PASSWORD"
echo "📍 연결 URL: jdbc:postgresql://localhost:5432/$DB_NAME"
echo ""
echo "🚀 이제 Backend 애플리케이션을 실행할 수 있습니다!"
echo "   ./gradlew bootRun"
echo "=========================================="

echo ""
log_info "설정이 완료되었습니다. Backend 애플리케이션을 실행해보세요!"
