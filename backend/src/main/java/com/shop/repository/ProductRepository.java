package com.shop.repository;

import com.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 상품 데이터에 대한 데이터 접근을 담당하는 Repository 인터페이스
 * 
 * 이 인터페이스는 Spring Data JPA의 JpaRepository를 상속받아
 * 기본적인 CRUD 작업과 추가적인 쿼리 메서드를 제공합니다.
 * 
 * @Repository 어노테이션은 이 클래스가 데이터 접근 계층의 컴포넌트임을 명시합니다.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // =====================================================
    // Spring Data JPA가 자동으로 구현하는 기본 메서드들
    // =====================================================
    // 
    // JpaRepository<Product, Long>를 상속받음으로써 다음 메서드들을 자동으로 사용할 수 있습니다:
    // - save(Product entity): 엔티티 저장/수정
    // - findById(Long id): ID로 엔티티 조회
    // - findAll(): 모든 엔티티 조회
    // - deleteById(Long id): ID로 엔티티 삭제
    // - count(): 전체 엔티티 개수 조회
    // - existsById(Long id): ID로 엔티티 존재 여부 확인

    // =====================================================
    // 메서드 이름으로 쿼리 생성 (Query Method)
    // =====================================================
    // Spring Data JPA는 메서드 이름을 분석하여 자동으로 SQL 쿼리를 생성합니다.
    // 메서드 이름 규칙: findBy + 필드명 + 조건

    /**
     * 상품명으로 상품을 조회하는 메서드
     * 
     * 메서드 이름 규칙: findBy + 필드명
     * 자동으로 생성되는 SQL: SELECT * FROM products WHERE name = ?
     * 
     * @param name 조회할 상품명
     * @return 상품명이 일치하는 상품 목록
     */
    List<Product> findByName(String name);

    /**
     * 상품명에 특정 문자열이 포함된 상품들을 조회하는 메서드
     * 
     * 메서드 이름 규칙: findBy + 필드명 + Containing
     * 자동으로 생성되는 SQL: SELECT * FROM products WHERE name LIKE %?%
     * 
     * @param name 포함될 상품명 (부분 문자열)
     * @return 상품명에 해당 문자열이 포함된 상품 목록
     */
    List<Product> findByNameContaining(String name);

    /**
     * 설명에 특정 문자열이 포함된 상품들을 조회하는 메서드
     * 
     * @param description 포함될 설명 (부분 문자열)
     * @return 설명에 해당 문자열이 포함된 상품 목록
     */
    List<Product> findByDescriptionContaining(String description);

    /**
     * 특정 가격 범위의 상품들을 조회하는 메서드
     * 
     * 메서드 이름 규칙: findBy + 필드명 + Between
     * 자동으로 생성되는 SQL: SELECT * FROM products WHERE price BETWEEN ? AND ?
     * 
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 가격 범위에 해당하는 상품 목록
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 특정 가격보다 낮은 상품들을 조회하는 메서드
     * 
     * @param price 기준 가격
     * @return 기준 가격보다 낮은 상품 목록
     */
    List<Product> findByPriceLessThan(BigDecimal price);

    /**
     * 특정 가격보다 높은 상품들을 조회하는 메서드
     * 
     * @param price 기준 가격
     * @return 기준 가격보다 높은 상품 목록
     */
    List<Product> findByPriceGreaterThan(BigDecimal price);

    /**
     * 상품명과 설명으로 상품을 조회하는 메서드
     * 
     * 메서드 이름 규칙: findBy + 필드명 + And + 필드명
     * 자동으로 생성되는 SQL: SELECT * FROM products WHERE name = ? AND description = ?
     * 
     * @param name 상품명
     * @param description 설명
     * @return 조건에 맞는 상품 목록
     */
    List<Product> findByNameAndDescription(String name, String description);

    /**
     * 상품명 또는 설명에 특정 문자열이 포함된 상품들을 조회하는 메서드
     * 
     * 메서드 이름 규칙: findBy + 필드명 + Or + 필드명 + Containing
     * 
     * @param name 상품명에 포함될 문자열
     * @param description 설명에 포함될 문자열
     * @return 조건에 맞는 상품 목록
     */
    List<Product> findByNameContainingOrDescriptionContaining(String name, String description);

    /**
     * 상품명으로 상품의 존재 여부를 확인하는 메서드
     * 
     * @param name 확인할 상품명
     * @return 상품이 존재하면 true, 존재하지 않으면 false
     */
    boolean existsByName(String name);

    /**
     * 상품명으로 상품 개수를 조회하는 메서드
     * 
     * @param name 조회할 상품명
     * @return 해당 상품명을 가진 상품의 개수
     */
    long countByName(String name);

    // =====================================================
    // @Query 어노테이션을 사용한 커스텀 쿼리
    // =====================================================
    // 복잡한 쿼리나 성능 최적화가 필요한 경우 직접 SQL을 작성할 수 있습니다.

    /**
     * 가격이 평균 가격보다 높은 상품들을 조회하는 메서드
     * 
     * @Query 어노테이션을 사용하여 직접 SQL 쿼리를 작성합니다.
     * 서브쿼리를 사용하여 평균 가격을 계산하고, 그보다 높은 가격의 상품을 조회합니다.
     * 
     * @return 평균 가격보다 높은 상품 목록
     */
    @Query("SELECT p FROM Product p WHERE p.price > (SELECT AVG(p2.price) FROM Product p2)")
    List<Product> findProductsAboveAveragePrice();

    /**
     * 특정 가격 범위의 상품들을 가격 순으로 정렬하여 조회하는 메서드
     * 
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 가격 순으로 정렬된 상품 목록
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price ASC")
    List<Product> findProductsByPriceRangeOrderByPrice(@Param("minPrice") BigDecimal minPrice, 
                                                      @Param("maxPrice") BigDecimal maxPrice);

    /**
     * 상품명에 특정 문자열이 포함된 상품들을 생성일 순으로 정렬하여 조회하는 메서드
     * 
     * @param name 포함될 상품명
     * @return 생성일 순으로 정렬된 상품 목록
     */
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name% ORDER BY p.createdAt DESC")
    List<Product> findProductsByNameOrderByCreatedAt(@Param("name") String name);

    /**
     * 특정 가격 이상의 상품 개수를 조회하는 메서드
     * 
     * @param price 기준 가격
     * @return 기준 가격 이상의 상품 개수
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.price >= :price")
    long countProductsByPriceGreaterThanEqual(@Param("price") BigDecimal price);

    // =====================================================
    // Native SQL 쿼리 예시 (필요시 사용)
    // =====================================================
    // 복잡한 데이터베이스 특화 쿼리가 필요한 경우 사용할 수 있습니다.

    /**
     * Native SQL을 사용하여 상품명으로 상품을 조회하는 메서드
     * 
     * @Query 어노테이션에 nativeQuery = true를 설정하면
     * 데이터베이스의 네이티브 SQL을 직접 사용할 수 있습니다.
     * 
     * @param name 조회할 상품명
     * @return 상품명이 일치하는 상품 목록
     */
    @Query(value = "SELECT * FROM products WHERE name = :name", nativeQuery = true)
    List<Product> findByNameNative(@Param("name") String name);
}
