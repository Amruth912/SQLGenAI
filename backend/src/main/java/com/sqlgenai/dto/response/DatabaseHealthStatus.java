package com.sqlgenai.dto.response;

public record DatabaseHealthStatus(
        boolean connected,
        String databaseName,
        String host,
        int port,
        Long latencyMs,
        String message
) {
    public static DatabaseHealthStatus connected(String databaseName, String host, int port, long latencyMs) {
        return new DatabaseHealthStatus(true, databaseName, host, port, latencyMs, "Successfully connected to PostgreSQL");
    }

    public static DatabaseHealthStatus disconnected(String databaseName, String host, int port, String message) {
        return new DatabaseHealthStatus(false, databaseName, host, port, null, message);
    }
}
