-- =====================================================
-- Simple Shop Backend - PostgreSQL 초기화 스크립트
-- =====================================================
-- 이 스크립트는 PostgreSQL 데이터베이스를 초기화하고
-- 필요한 사용자, 데이터베이스, 권한을 설정합니다.

-- =====================================================
-- 1. 데이터베이스 생성
-- =====================================================
-- simple_shop 데이터베이스가 존재하지 않으면 생성
-- IF NOT EXISTS는 PostgreSQL 9.5+ 버전에서 지원됩니다.
SELECT 'CREATE DATABASE simple_shop'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'simple_shop')\gexec

-- =====================================================
-- 2. 사용자 생성 및 권한 설정
-- =====================================================
-- shop_user 사용자가 존재하지 않으면 생성
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'shop_user') THEN
        CREATE USER shop_user WITH PASSWORD 'shop_password';
    END IF;
END
$$;

-- =====================================================
-- 3. 데이터베이스 권한 설정
-- =====================================================
-- simple_shop 데이터베이스에 대한 모든 권한을 shop_user에게 부여
GRANT ALL PRIVILEGES ON DATABASE simple_shop TO shop_user;

-- =====================================================
-- 4. 스키마 권한 설정
-- =====================================================
-- simple_shop 데이터베이스에 연결하여 스키마 권한 설정
\c simple_shop;

-- public 스키마에 대한 모든 권한을 shop_user에게 부여
GRANT ALL ON SCHEMA public TO shop_user;

-- 기존 테이블들에 대한 모든 권한을 shop_user에게 부여
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO shop_user;

-- 기존 시퀀스들에 대한 모든 권한을 shop_user에게 부여
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO shop_user;

-- 앞으로 생성될 테이블들에 대한 기본 권한 설정
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO shop_user;

-- 앞으로 생성될 시퀀스들에 대한 기본 권한 설정
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO shop_user;

-- =====================================================
-- 5. 초기 데이터 삽입 (선택사항)
-- =====================================================
-- 테스트용 샘플 상품 데이터를 삽입할 수 있습니다.
-- 주석을 해제하여 사용하세요.

/*
-- products 테이블이 존재하는지 확인 후 샘플 데이터 삽입
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'products') THEN
        -- 기존 데이터가 없을 때만 샘플 데이터 삽입
        IF NOT EXISTS (SELECT 1 FROM products LIMIT 1) THEN
            INSERT INTO products (name, description, price, created_at, updated_at) VALUES
            ('샘플 상품 1', '첫 번째 샘플 상품입니다.', 10000.00, NOW(), NOW()),
            ('샘플 상품 2', '두 번째 샘플 상품입니다.', 20000.00, NOW(), NOW()),
            ('샘플 상품 3', '세 번째 샘플 상품입니다.', 30000.00, NOW(), NOW());
            
            RAISE NOTICE '샘플 상품 데이터가 삽입되었습니다.';
        ELSE
            RAISE NOTICE '이미 상품 데이터가 존재합니다.';
        END IF;
    END IF;
END
$$;
*/

-- =====================================================
-- 6. 설정 완료 메시지
-- =====================================================
DO $$
BEGIN
    RAISE NOTICE '==========================================';
    RAISE NOTICE '🚀 Simple Shop Database 초기화 완료!';
    RAISE NOTICE '==========================================';
    RAISE NOTICE '📊 데이터베이스: simple_shop';
    RAISE NOTICE '👤 사용자: shop_user';
    RAISE NOTICE '🔑 비밀번호: shop_password';
    RAISE NOTICE '📍 연결 URL: jdbc:postgresql://localhost:5432/simple_shop';
    RAISE NOTICE '==========================================';
END
$$;
