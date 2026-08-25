package com.sqlgenai.dto.response;

import java.util.List;
import java.util.Map;

public record QueryResponse(
    String generatedSql,
    List<String> columns,
    List<Map<String, Object>> rows,
    int rowCount,
    long executionTimeMs,
    String error
) {
    public static QueryResponse success(String sql, List<String> columns, List<Map<String, Object>> rows, long executionTimeMs) {
        return new QueryResponse(sql, columns, rows, rows.size(), executionTimeMs, null);
    }
    
    public static QueryResponse error(String sql, String error) {
        return new QueryResponse(sql, null, null, 0, 0, error);
    }
}
