package com.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Simple Shop 애플리케이션의 메인 클래스
 * 
 * 이 클래스는 Spring Boot 애플리케이션을 시작하는 진입점입니다.
 * @SpringBootApplication 어노테이션은 다음 기능들을 자동으로 설정합니다:
 * - @Configuration: 이 클래스가 Spring 설정 클래스임을 명시
 * - @EnableAutoConfiguration: Spring Boot의 자동 설정 활성화
 * - @ComponentScan: com.shop 패키지 하위의 컴포넌트들을 자동으로 스캔
 */
@SpringBootApplication
public class ShopApplication {

    /**
     * 애플리케이션의 메인 메서드
     * 
     * @param args 명령행 인수 (사용하지 않음)
     */
    public static void main(String[] args) {
        // SpringApplication.run()을 호출하여 Spring Boot 애플리케이션을 시작합니다.
        // 이 메서드는 다음 작업들을 수행합니다:
        // 1. Spring ApplicationContext를 생성
        // 2. 자동 설정을 적용
        // 3. 컴포넌트 스캔을 수행
        // 4. 내장 웹 서버(기본적으로 Tomcat)를 시작
        // 5. 애플리케이션을 실행 상태로 전환
        SpringApplication.run(ShopApplication.class, args);
        
        // 애플리케이션이 성공적으로 시작되면 콘솔에 메시지를 출력합니다.
        System.out.println("==========================================");
        System.out.println("🚀 Simple Shop Backend Started Successfully!");
        System.out.println("📍 Application URL: http://localhost:8080");
        System.out.println("📚 API Documentation: http://localhost:8080/api");
        System.out.println("🔍 Health Check: http://localhost:8080/actuator/health");
        System.out.println("==========================================");
    }
}
