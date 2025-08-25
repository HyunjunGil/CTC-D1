package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

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

    // =====================================================
    // CORS 정책 설정
    // =====================================================
    
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
                .allowedOriginPatterns("*")  // 모든 오리진 허용 (개발 환경용)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")  // 허용할 HTTP 메서드
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true)  // 쿠키 및 인증 정보 허용
                .maxAge(3600);  // CORS 프리플라이트 요청 캐시 시간 (초)
    }

    // =====================================================
    // CORS 설정 소스 Bean
    // =====================================================
    
    /**
     * CORS 설정 소스를 생성하는 Bean 메서드
     * 
     * 이 메서드는 CorsConfigurationSource 타입의 Bean을 생성하여
     * 더 세밀한 CORS 정책을 설정할 수 있게 합니다.
     * 
     * @return CORS 설정 소스
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 오리진 설정
        // 개발 환경에서는 모든 오리진을 허용하고,
        // 운영 환경에서는 특정 도메인만 허용하는 것이 좋습니다.
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList(
            "GET",      // 데이터 조회
            "POST",     // 데이터 생성
            "PUT",      // 데이터 수정
            "DELETE",   // 데이터 삭제
            "OPTIONS",  // CORS 프리플라이트 요청
            "PATCH"     // 부분 데이터 수정
        ));
        
        // 허용할 HTTP 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList(
            "Origin",           // 요청 오리진
            "Content-Type",     // 콘텐츠 타입
            "Accept",          // 응답으로 받을 수 있는 콘텐츠 타입
            "Authorization",    // 인증 정보
            "X-Requested-With", // AJAX 요청 식별
            "Cache-Control",    // 캐시 제어
            "Pragma"           // 캐시 제어 (HTTP/1.0 호환성)
        ));
        
        // 노출할 HTTP 헤더 설정
        // 프론트엔드에서 접근할 수 있는 응답 헤더를 지정합니다.
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",      // 허용된 오리진
            "Access-Control-Allow-Credentials", // 인증 정보 허용 여부
            "Access-Control-Allow-Methods",     // 허용된 HTTP 메서드
            "Access-Control-Allow-Headers",     // 허용된 HTTP 헤더
            "Access-Control-Max-Age"           // 프리플라이트 요청 캐시 시간
        ));
        
        // 인증 정보 허용 설정
        // 쿠키, 세션, Authorization 헤더 등을 포함한 요청을 허용합니다.
        configuration.setAllowCredentials(true);
        
        // CORS 프리플라이트 요청 캐시 시간 설정 (초)
        // 브라우저가 실제 요청 전에 보내는 OPTIONS 요청의 결과를 캐시하는 시간입니다.
        configuration.setMaxAge(3600L);
        
        // URL 패턴별 CORS 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    // =====================================================
    // 환경별 CORS 설정
    // =====================================================
    
    /**
     * 개발 환경용 CORS 설정
     * 
     * 이 메서드는 개발 환경에서 사용할 수 있는
     * 더 관대한 CORS 정책을 설정합니다.
     * 
     * @return 개발 환경용 CORS 설정
     */
    @Bean
    public CorsConfigurationSource developmentCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 개발 환경에서는 모든 오리진 허용
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 모든 HTTP 메서드 허용
        configuration.setAllowedMethods(Arrays.asList("*"));
        
        // 모든 헤더 허용
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 모든 헤더 노출
        configuration.setExposedHeaders(Arrays.asList("*"));
        
        // 인증 정보 허용
        configuration.setAllowCredentials(true);
        
        // 프리플라이트 요청 캐시 시간 설정
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * 운영 환경용 CORS 설정
     * 
     * 이 메서드는 운영 환경에서 사용할 수 있는
     * 보안을 강화한 CORS 정책을 설정합니다.
     * 
     * @return 운영 환경용 CORS 설정
     */
    @Bean
    public CorsConfigurationSource productionCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 운영 환경에서는 특정 도메인만 허용
        // 실제 운영 환경에서는 실제 프론트엔드 도메인을 지정해야 합니다.
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "https://yourdomain.com",      // 실제 도메인으로 변경
            "https://www.yourdomain.com"   // www 서브도메인
        ));
        
        // 필요한 HTTP 메서드만 허용
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE"
        ));
        
        // 필요한 헤더만 허용
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type", "Authorization", "X-Requested-With"
        ));
        
        // 필요한 헤더만 노출
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // 인증 정보 허용
        configuration.setAllowCredentials(true);
        
        // 프리플라이트 요청 캐시 시간 설정
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
