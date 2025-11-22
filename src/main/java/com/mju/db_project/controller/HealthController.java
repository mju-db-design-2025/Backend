package com.mju.db_project.controller;

import com.mju.db_project.dto.response.HealthCheckResponse;
import com.mju.db_project.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Health Check", description = "데이터베이스 상태 확인 API")
public class HealthController {

    private final HealthService healthService;

    @GetMapping("/health")
    @Operation(summary = "데이터베이스 상태 확인", description = "데이터베이스 연결 상태와 응답 시간을 확인합니다.")
    public ResponseEntity<HealthCheckResponse> checkHealth() {
        return ResponseEntity.ok(healthService.checkDatabaseHealth());
    }
}

