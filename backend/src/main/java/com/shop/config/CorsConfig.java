package com.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS (Cross-Origin Resource Sharing) 정책을 설정하는 클래스
 * 
 * 이 클래스는 프론트엔드 애플리케이션이 다른 도메인(포트)에서
 * 백엔드 API에 접근할 수 있도록 허용하는 설정을 제공합니다.
 * 
 * @Configuration 어노테이션은 이 클래스가 Spring 설정 클래스임을 명시합니다.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * CORS 정책을 설정하는 메서드
     * 
     * 이 메서드는 WebMvcConfigurer 인터페이스를 구현하여
     * Spring MVC의 CORS 설정을 커스터마이징합니다.
     * 
     * @param registry CORS 레지스트리
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해 CORS 정책 적용
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")  // 로컬 개발 환경 오리진 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")  // 허용할 HTTP 메서드
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true)  // 쿠키 및 인증 정보 허용
                .maxAge(3600);  // CORS 프리플라이트 요청 캐시 시간 (초)
    }
}
