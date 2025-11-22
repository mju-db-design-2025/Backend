package com.mju.db_project.service;

import com.mju.db_project.dto.response.HealthCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthService {

    private final DataSource dataSource;

    public HealthCheckResponse checkDatabaseHealth() {
        boolean isHealthy = false;
        String message = "";
        long responseTime = 0;

        try {
            long startTime = System.currentTimeMillis();
            
            try (Connection connection = dataSource.getConnection()) {
                // 간단한 쿼리로 연결 테스트
                boolean isValid = connection.isValid(5); // 5초 타임아웃
                responseTime = System.currentTimeMillis() - startTime;
                
                if (isValid) {
                    isHealthy = true;
                    message = "Database connection is healthy";
                } else {
                    message = "Database connection validation failed";
                }
            }
        } catch (SQLException e) {
            log.error("Database health check failed", e);
            message = "Database connection error: " + e.getMessage();
        }

        return HealthCheckResponse.builder()
                .status(isHealthy ? "UP" : "DOWN")
                .database(isHealthy ? "healthy" : "unhealthy")
                .message(message)
                .timestamp(LocalDateTime.now())
                .responseTime(responseTime + "ms")
                .build();
    }
}

