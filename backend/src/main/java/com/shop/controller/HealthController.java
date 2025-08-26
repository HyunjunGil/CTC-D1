package com.shop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check를 위한 간단한 컨트롤러
 * Docker Compose의 health check에서 사용됩니다.
 */
@RestController
public class HealthController {

    /**
     * 애플리케이션 상태를 확인하는 엔드포인트
     * @return 애플리케이션 상태 정보
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "Simple Shop Backend");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }

    /**
     * 루트 경로에 대한 간단한 응답
     * @return 애플리케이션 정보
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, String>> root() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "Simple Shop Backend is running!");
        info.put("status", "OK");
        
        return ResponseEntity.ok(info);
    }
}
