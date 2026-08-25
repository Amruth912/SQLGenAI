package com.sqlgenai.dto.response;

public record ColumnSchemaResponse(
        String name,
        String dataType,
        boolean nullable,
        boolean primaryKey,
        String defaultValue,
        Integer ordinalPosition
) {
    public static ColumnSchemaResponse of(
            String name,
            String dataType,
            boolean nullable,
            boolean primaryKey,
            String defaultValue,
            Integer ordinalPosition
    ) {
        return new ColumnSchemaResponse(name, dataType, nullable, primaryKey, defaultValue, ordinalPosition);
    }
}
