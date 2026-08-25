package com.sqlgenai.dto.response;

import java.time.Instant;
import java.util.Map;

public record HealthResponse(
        String status,
        String service,
        String version,
        Instant timestamp,
        DatabaseHealthStatus database,
        Map<String, Object> details
) {
    public static HealthResponse of(
            String status,
            String service,
            String version,
            DatabaseHealthStatus database,
            Map<String, Object> details
    ) {
        return new HealthResponse(status, service, version, Instant.now(), database, details);
    }
}
