package com.sqlgenai.dto.response;

import java.time.Instant;
import java.util.List;

public record SchemaResponse(
        String databaseName,
        String schemaName,
        int tableCount,
        Instant retrievedAt,
        List<TableSchemaResponse> tables
) {
    public static SchemaResponse of(
            String databaseName,
            String schemaName,
            List<TableSchemaResponse> tables
    ) {
        return new SchemaResponse(
                databaseName,
                schemaName,
                tables != null ? tables.size() : 0,
                Instant.now(),
                tables != null ? tables : List.of()
        );
    }
}
