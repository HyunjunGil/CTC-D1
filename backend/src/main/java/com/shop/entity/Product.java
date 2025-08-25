package com.shop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 상품 정보를 저장하는 JPA 엔티티 클래스
 * 
 * 이 클래스는 데이터베이스의 'products' 테이블과 매핑됩니다.
 * JPA 어노테이션을 사용하여 테이블 구조와 컬럼을 정의합니다.
 */
@Entity
@Table(name = "products")
public class Product {

    /**
     * 상품의 고유 식별자 (Primary Key)
     * 
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     * - 데이터베이스가 자동으로 증가하는 값을 할당
     * - PostgreSQL의 SERIAL 타입과 동일
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 상품명
     * 
     * @NotBlank: null, 빈 문자열, 공백만 있는 문자열을 허용하지 않음
     * @Size: 문자열의 길이 제한 (최소 1자, 최대 100자)
     * @Column: 데이터베이스 컬럼 설정
     */
    @NotBlank(message = "상품명은 필수입니다.")
    @Size(min = 1, max = 100, message = "상품명은 1자 이상 100자 이하여야 합니다.")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 상품 설명
     * 
     * @Size: 문자열의 길이 제한 (최대 1000자)
     * @Column: nullable = true로 설정하여 선택사항으로 만듦
     */
    @Size(max = 1000, message = "상품 설명은 1000자 이하여야 합니다.")
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 상품 가격
     * 
     * @NotNull: null 값을 허용하지 않음
     * @Positive: 양수만 허용 (0보다 큰 값)
     * @Column: precision = 10, scale = 2로 설정하여 소수점 2자리까지 저장
     */
    @NotNull(message = "가격은 필수입니다.")
    @Positive(message = "가격은 양수여야 합니다.")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 상품 생성 시간
     * 
     * @CreationTimestamp: JPA가 엔티티를 처음 저장할 때 자동으로 현재 시간 설정
     * @Column: updatable = false로 설정하여 수정 시 변경되지 않음
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 상품 수정 시간
     * 
     * @UpdateTimestamp: JPA가 엔티티를 수정할 때마다 자동으로 현재 시간 설정
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // =====================================================
    // 생성자
    // =====================================================

    /**
     * 기본 생성자
     * JPA에서 엔티티를 생성할 때 필요합니다.
     */
    public Product() {
    }

    /**
     * 모든 필드를 포함한 생성자
     * 
     * @param name 상품명
     * @param description 상품 설명
     * @param price 상품 가격
     */
    public Product(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // =====================================================
    // Getter와 Setter 메서드
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // =====================================================
    // equals, hashCode, toString 메서드
    // =====================================================

    /**
     * 객체의 동등성을 비교하는 메서드
     * ID 값만으로 동등성을 판단합니다.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    /**
     * 객체의 해시 코드를 생성하는 메서드
     * ID 값의 해시 코드를 반환합니다.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * 객체의 문자열 표현을 생성하는 메서드
     * 디버깅과 로깅에 유용합니다.
     */
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
