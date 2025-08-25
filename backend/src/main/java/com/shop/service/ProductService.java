package com.shop.service;

import com.shop.entity.Product;
import com.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 상품 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 
 * 이 클래스는 컨트롤러와 리포지토리 사이의 중간 계층으로,
 * 비즈니스 규칙과 트랜잭션을 관리합니다.
 * 
 * @Service 어노테이션은 이 클래스가 비즈니스 로직을 담당하는 서비스 컴포넌트임을 명시합니다.
 */
@Service
@Transactional
public class ProductService {

    // =====================================================
    // 의존성 주입
    // =====================================================
    
    /**
     * 상품 데이터에 대한 데이터 접근을 담당하는 리포지토리
     * 
     * @Autowired 어노테이션을 사용하여 Spring이 자동으로
     * ProductRepository 구현체를 주입합니다.
     */
    private final ProductRepository productRepository;

    /**
     * 생성자를 통한 의존성 주입
     * 
     * @param productRepository 상품 리포지토리
     */
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // =====================================================
    // 기본 CRUD 작업
    // =====================================================

    /**
     * 새로운 상품을 생성하는 메서드
     * 
     * @Transactional 어노테이션이 메서드 레벨에 적용되어
     * 이 메서드 내에서 발생하는 모든 데이터베이스 작업이
     * 하나의 트랜잭션으로 처리됩니다.
     * 
     * @param product 생성할 상품 정보
     * @return 저장된 상품 정보 (ID가 할당됨)
     */
    @Transactional
    public Product createProduct(Product product) {
        // 입력 데이터 검증
        validateProduct(product);
        
        // 상품명 중복 확인
        if (productRepository.existsByName(product.getName())) {
            throw new IllegalArgumentException("이미 존재하는 상품명입니다: " + product.getName());
        }
        
        // 상품 저장 및 반환
        return productRepository.save(product);
    }

    /**
     * 모든 상품을 조회하는 메서드
     * 
     * @return 전체 상품 목록
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * ID로 특정 상품을 조회하는 메서드
     * 
     * @param id 조회할 상품의 ID
     * @return 상품 정보 (Optional로 래핑됨)
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * 상품 정보를 수정하는 메서드
     * 
     * @param id 수정할 상품의 ID
     * @param updatedProduct 수정된 상품 정보
     * @return 수정된 상품 정보
     * @throws IllegalArgumentException 상품이 존재하지 않는 경우
     */
    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        // 기존 상품 존재 여부 확인
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + id));
        
        // 입력 데이터 검증
        validateProduct(updatedProduct);
        
        // 상품명 중복 확인 (자기 자신 제외)
        if (!existingProduct.getName().equals(updatedProduct.getName()) &&
            productRepository.existsByName(updatedProduct.getName())) {
            throw new IllegalArgumentException("이미 존재하는 상품명입니다: " + updatedProduct.getName());
        }
        
        // 상품 정보 업데이트
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        
        // 업데이트된 상품 저장 및 반환
        return productRepository.save(existingProduct);
    }

    /**
     * 상품을 삭제하는 메서드
     * 
     * @param id 삭제할 상품의 ID
     * @throws IllegalArgumentException 상품이 존재하지 않는 경우
     */
    @Transactional
    public void deleteProduct(Long id) {
        // 상품 존재 여부 확인
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + id);
        }
        
        // 상품 삭제
        productRepository.deleteById(id);
    }

    // =====================================================
    // 검색 및 필터링 메서드
    // =====================================================

    /**
     * 상품명으로 상품을 검색하는 메서드
     * 
     * @param name 검색할 상품명 (부분 문자열)
     * @return 검색 결과 상품 목록
     */
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByNameContaining(name.trim());
    }

    /**
     * 설명으로 상품을 검색하는 메서드
     * 
     * @param description 검색할 설명 (부분 문자열)
     * @return 검색 결과 상품 목록
     */
    @Transactional(readOnly = true)
    public List<Product> searchProductsByDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByDescriptionContaining(description.trim());
    }

    /**
     * 가격 범위로 상품을 검색하는 메서드
     * 
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 가격 범위에 해당하는 상품 목록
     */
    @Transactional(readOnly = true)
    public List<Product> searchProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        // 가격 범위 검증
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("최소 가격과 최대 가격을 모두 입력해주세요.");
        }
        
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("최소 가격은 최대 가격보다 작아야 합니다.");
        }
        
        if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * 상품명 또는 설명으로 상품을 검색하는 메서드
     * 
     * @param keyword 검색할 키워드
     * @return 검색 결과 상품 목록
     */
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        
        String trimmedKeyword = keyword.trim();
        return productRepository.findByNameContainingOrDescriptionContaining(trimmedKeyword, trimmedKeyword);
    }

    // =====================================================
    // 통계 및 분석 메서드
    // =====================================================

    /**
     * 전체 상품 개수를 조회하는 메서드
     * 
     * @return 전체 상품 개수
     */
    @Transactional(readOnly = true)
    public long getTotalProductCount() {
        return productRepository.count();
    }

    /**
     * 특정 가격 이상의 상품 개수를 조회하는 메서드
     * 
     * @param price 기준 가격
     * @return 기준 가격 이상의 상품 개수
     */
    @Transactional(readOnly = true)
    public long getProductCountByPriceGreaterThanEqual(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        return productRepository.countProductsByPriceGreaterThanEqual(price);
    }

    /**
     * 평균 가격보다 높은 상품들을 조회하는 메서드
     * 
     * @return 평균 가격보다 높은 상품 목록
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsAboveAveragePrice() {
        return productRepository.findProductsAboveAveragePrice();
    }

    // =====================================================
    // 유틸리티 메서드
    // =====================================================

    /**
     * 상품 정보의 유효성을 검증하는 메서드
     * 
     * @param product 검증할 상품 정보
     * @throws IllegalArgumentException 유효하지 않은 데이터인 경우
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("상품 정보가 null입니다.");
        }
        
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        
        if (product.getName().trim().length() > 100) {
            throw new IllegalArgumentException("상품명은 100자를 초과할 수 없습니다.");
        }
        
        if (product.getDescription() != null && product.getDescription().trim().length() > 1000) {
            throw new IllegalArgumentException("상품 설명은 1000자를 초과할 수 없습니다.");
        }
        
        if (product.getPrice() == null) {
            throw new IllegalArgumentException("가격은 필수입니다.");
        }
        
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        }
    }

    /**
     * 상품이 존재하는지 확인하는 메서드
     * 
     * @param id 확인할 상품의 ID
     * @return 상품이 존재하면 true, 존재하지 않으면 false
     */
    @Transactional(readOnly = true)
    public boolean productExists(Long id) {
        return productRepository.existsById(id);
    }

    /**
     * 상품명으로 상품이 존재하는지 확인하는 메서드
     * 
     * @param name 확인할 상품명
     * @return 상품이 존재하면 true, 존재하지 않으면 false
     */
    @Transactional(readOnly = true)
    public boolean productExistsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return productRepository.existsByName(name.trim());
    }
}
