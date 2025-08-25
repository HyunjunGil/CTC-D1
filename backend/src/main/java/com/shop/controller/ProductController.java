package com.shop.controller;

import com.shop.entity.Product;
import com.shop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 상품 관련 REST API 엔드포인트를 제공하는 컨트롤러 클래스
 * 
 * 이 클래스는 HTTP 요청을 받아서 적절한 서비스 메서드를 호출하고,
 * 결과를 HTTP 응답으로 반환합니다.
 * 
 * @RestController 어노테이션은 이 클래스가 REST API 컨트롤러임을 명시하며,
 * @RequestMapping 어노테이션으로 기본 URL 경로를 설정합니다.
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // CORS 설정 (개발 환경용)
public class ProductController {

    // =====================================================
    // 의존성 주입
    // =====================================================
    
    /**
     * 상품 관련 비즈니스 로직을 처리하는 서비스
     */
    private final ProductService productService;

    /**
     * 생성자를 통한 의존성 주입
     * 
     * @param productService 상품 서비스
     */
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // =====================================================
    // 기본 CRUD 작업 API
    // =====================================================

    /**
     * 모든 상품을 조회하는 API
     * 
     * HTTP GET 요청: /api/products
     * 
     * @return 전체 상품 목록과 HTTP 200 상태 코드
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * ID로 특정 상품을 조회하는 API
     * 
     * HTTP GET 요청: /api/products/{id}
     * 
     * @param id 조회할 상품의 ID
     * @return 상품 정보와 HTTP 200 상태 코드, 또는 HTTP 404 상태 코드
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 새로운 상품을 생성하는 API
     * 
     * HTTP POST 요청: /api/products
     * 요청 본문: 상품 정보 (JSON)
     * 
     * @param product 생성할 상품 정보
     * @return 생성된 상품 정보와 HTTP 201 상태 코드
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (IllegalArgumentException e) {
            // 비즈니스 규칙 위반 시 400 Bad Request 반환
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 상품 정보를 수정하는 API
     * 
     * HTTP PUT 요청: /api/products/{id}
     * 요청 본문: 수정된 상품 정보 (JSON)
     * 
     * @param id 수정할 상품의 ID
     * @param updatedProduct 수정된 상품 정보
     * @return 수정된 상품 정보와 HTTP 200 상태 코드, 또는 HTTP 404 상태 코드
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, 
                                               @Valid @RequestBody Product updatedProduct) {
        try {
            Product product = productService.updateProduct(id, updatedProduct);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("상품을 찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * 상품을 삭제하는 API
     * 
     * HTTP DELETE 요청: /api/products/{id}
     * 
     * @param id 삭제할 상품의 ID
     * @return HTTP 204 상태 코드 (성공), 또는 HTTP 404 상태 코드
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // =====================================================
    // 검색 및 필터링 API
    // =====================================================

    /**
     * 상품명으로 상품을 검색하는 API
     * 
     * HTTP GET 요청: /api/products/search/name?name={상품명}
     * 
     * @param name 검색할 상품명
     * @return 검색 결과 상품 목록과 HTTP 200 상태 코드
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    /**
     * 설명으로 상품을 검색하는 API
     * 
     * HTTP GET 요청: /api/products/search/description?description={설명}
     * 
     * @param description 검색할 설명
     * @return 검색 결과 상품 목록과 HTTP 200 상태 코드
     */
    @GetMapping("/search/description")
    public ResponseEntity<List<Product>> searchProductsByDescription(@RequestParam String description) {
        List<Product> products = productService.searchProductsByDescription(description);
        return ResponseEntity.ok(products);
    }

    /**
     * 가격 범위로 상품을 검색하는 API
     * 
     * HTTP GET 요청: /api/products/search/price?minPrice={최소가격}&maxPrice={최대가격}
     * 
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 검색 결과 상품 목록과 HTTP 200 상태 코드, 또는 HTTP 400 상태 코드
     */
    @GetMapping("/search/price")
    public ResponseEntity<List<Product>> searchProductsByPriceRange(@RequestParam BigDecimal minPrice,
                                                                   @RequestParam BigDecimal maxPrice) {
        try {
            List<Product> products = productService.searchProductsByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 키워드로 상품을 검색하는 API (상품명 또는 설명)
     * 
     * HTTP GET 요청: /api/products/search?keyword={키워드}
     * 
     * @param keyword 검색할 키워드
     * @return 검색 결과 상품 목록과 HTTP 200 상태 코드
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam(required = false) String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    // =====================================================
    // 통계 및 분석 API
    // =====================================================

    /**
     * 전체 상품 개수를 조회하는 API
     * 
     * HTTP GET 요청: /api/products/count
     * 
     * @return 전체 상품 개수와 HTTP 200 상태 코드
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalProductCount() {
        long count = productService.getTotalProductCount();
        return ResponseEntity.ok(count);
    }

    /**
     * 특정 가격 이상의 상품 개수를 조회하는 API
     * 
     * HTTP GET 요청: /api/products/count/price?price={기준가격}
     * 
     * @param price 기준 가격
     * @return 상품 개수와 HTTP 200 상태 코드, 또는 HTTP 400 상태 코드
     */
    @GetMapping("/count/price")
    public ResponseEntity<Long> getProductCountByPriceGreaterThanEqual(@RequestParam BigDecimal price) {
        try {
            long count = productService.getProductCountByPriceGreaterThanEqual(price);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 평균 가격보다 높은 상품들을 조회하는 API
     * 
     * HTTP GET 요청: /api/products/above-average
     * 
     * @return 평균 가격보다 높은 상품 목록과 HTTP 200 상태 코드
     */
    @GetMapping("/above-average")
    public ResponseEntity<List<Product>> getProductsAboveAveragePrice() {
        List<Product> products = productService.getProductsAboveAveragePrice();
        return ResponseEntity.ok(products);
    }

    // =====================================================
    // 상태 확인 API
    // =====================================================

    /**
     * 상품이 존재하는지 확인하는 API
     * 
     * HTTP GET 요청: /api/products/{id}/exists
     * 
     * @param id 확인할 상품의 ID
     * @return 존재 여부와 HTTP 200 상태 코드
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> productExists(@PathVariable Long id) {
        boolean exists = productService.productExists(id);
        return ResponseEntity.ok(exists);
    }

    /**
     * 상품명으로 상품이 존재하는지 확인하는 API
     * 
     * HTTP GET 요청: /api/products/exists/name?name={상품명}
     * 
     * @param name 확인할 상품명
     * @return 존재 여부와 HTTP 200 상태 코드
     */
    @GetMapping("/exists/name")
    public ResponseEntity<Boolean> productExistsByName(@RequestParam String name) {
        boolean exists = productService.productExistsByName(name);
        return ResponseEntity.ok(exists);
    }

    // =====================================================
    // 예외 처리
    // =====================================================

    /**
     * IllegalArgumentException을 처리하는 메서드
     * 
     * @param e 발생한 예외
     * @return HTTP 400 Bad Request 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /**
     * 일반적인 예외를 처리하는 메서드
     * 
     * @param e 발생한 예외
     * @return HTTP 500 Internal Server Error 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 내부 오류가 발생했습니다: " + e.getMessage());
    }
}
