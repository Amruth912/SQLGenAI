package com.sqlgenai.dto.response;

import java.util.List;
import java.util.Map;

public record QueryResponse(
    String generatedSql,
    List<String> columns,
    List<Map<String, Object>> rows,
    int rowCount,
    long executionTimeMs,
    String error,
    String statementType,
    boolean readOnly,
    boolean requiresConfirmation,
    String riskLevel,
    String message
) {
    public static QueryResponse success(String sql, List<String> columns, List<Map<String, Object>> rows, long executionTimeMs) {
        return new QueryResponse(sql, columns, rows, rows.size(), executionTimeMs, null, "SELECT", true, false, "READ_ONLY", "Query executed successfully");
    }

    public static QueryResponse success(String sql, List<String> columns, List<Map<String, Object>> rows, long executionTimeMs, String statementType, boolean readOnly) {
        return new QueryResponse(sql, columns, rows, rows.size(), executionTimeMs, null, statementType, readOnly, false, readOnly ? "READ_ONLY" : "MUTATING", "Query executed successfully");
    }

    public static QueryResponse dmlSuccess(String sql, String statementType, int rowsAffected, long executionTimeMs) {
        List<String> columns = List.of("status", "rows_affected", "statement_type");
        List<Map<String, Object>> rows = List.of(Map.of(
                "status", "SUCCESS",
                "rows_affected", rowsAffected,
                "statement_type", statementType
        ));
        String msg = String.format("%s statement executed successfully (%d rows affected)", statementType, rowsAffected);
        return new QueryResponse(sql, columns, rows, rowsAffected, executionTimeMs, null, statementType, false, false, "EXECUTED", msg);
    }

    public static QueryResponse confirmationRequired(String sql, String statementType, String riskLevel) {
        String msg = String.format("Confirmation required: This %s statement will modify or alter database data (%s).", statementType, riskLevel);
        return new QueryResponse(sql, null, null, 0, 0, null, statementType, false, true, riskLevel, msg);
    }
    
    public static QueryResponse error(String sql, String error) {
        return new QueryResponse(sql, null, null, 0, 0, error, "UNKNOWN", false, false, "ERROR", error);
    }
}
