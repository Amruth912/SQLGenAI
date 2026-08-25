package com.sqlgenai.dto.response;

import java.util.List;

public record TableSchemaResponse(
        String name,
        String schema,
        Long estimatedRowCount,
        List<ColumnSchemaResponse> columns,
        List<ForeignKeyResponse> foreignKeys
) {
    public static TableSchemaResponse of(
            String name,
            String schema,
            Long estimatedRowCount,
            List<ColumnSchemaResponse> columns,
            List<ForeignKeyResponse> foreignKeys
    ) {
        return new TableSchemaResponse(name, schema, estimatedRowCount, columns, foreignKeys);
    }
}
