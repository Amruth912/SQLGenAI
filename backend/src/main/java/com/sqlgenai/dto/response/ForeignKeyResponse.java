package com.sqlgenai.dto.response;

public record ForeignKeyResponse(
        String column,
        String referencedTable,
        String referencedColumn,
        String constraintName
) {
    public static ForeignKeyResponse of(String column, String referencedTable, String referencedColumn, String constraintName) {
        return new ForeignKeyResponse(column, referencedTable, referencedColumn, constraintName);
    }
}
