package com.sqlgenai.controller;

import com.sqlgenai.dto.response.DatabaseHealthStatus;
import com.sqlgenai.dto.response.HealthResponse;
import com.sqlgenai.service.DatabaseHealthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @Value("${spring.application.name:sqlgenai-backend}")
    private String applicationName;

    @Value("${app.ai.provider:gemini}")
    private String aiProvider;

    private final DatabaseHealthService databaseHealthService;

    public HealthController(DatabaseHealthService databaseHealthService) {
        this.databaseHealthService = databaseHealthService;
    }

    @GetMapping
    public ResponseEntity<HealthResponse> getHealth() {
        DatabaseHealthStatus dbHealth = databaseHealthService.checkDatabaseConnectivity();
        String overallStatus = dbHealth.connected() ? "UP" : "DEGRADED";

        Map<String, Object> details = new HashMap<>();
        details.put("phase", "Phase 2 - Database Connection");
        details.put("aiProvider", aiProvider);
        details.put("securityEngine", "JSqlParser AST Validator Ready");

        HealthResponse response = HealthResponse.of(
                overallStatus,
                applicationName,
                "1.0.0",
                dbHealth,
                details
        );

        return ResponseEntity.ok(response);
    }
}
