package com.sqlgenai.service;

import com.sqlgenai.config.DatabaseProperties;
import com.sqlgenai.dto.response.ColumnSchemaResponse;
import com.sqlgenai.dto.response.ForeignKeyResponse;
import com.sqlgenai.dto.response.SchemaResponse;
import com.sqlgenai.dto.response.TableSchemaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SchemaIntrospectionService {

    private static final Logger log = LoggerFactory.getLogger(SchemaIntrospectionService.class);

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseProperties databaseProperties;

    public SchemaIntrospectionService(JdbcTemplate jdbcTemplate, DatabaseProperties databaseProperties) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseProperties = databaseProperties;
    }

    /**
     * Introspects the live PostgreSQL schema and returns structured table/column/FK metadata.
     *
     * @param schemaName target database schema (defaults to 'public')
     * @return SchemaResponse with all discovered tables and relationships
     */
    public SchemaResponse introspectSchema(String schemaName) {
        String targetSchema = (schemaName != null && !schemaName.isBlank()) ? schemaName : "public";
        String databaseName = databaseProperties.getDatabase();

        log.info("Starting schema introspection for schema='{}' in database='{}'", targetSchema, databaseName);

        List<String> tableNames = getTableNames(targetSchema);
        List<TableSchemaResponse> tables = new ArrayList<>();

        for (String tableName : tableNames) {
            Set<String> primaryKeys = getPrimaryKeys(targetSchema, tableName);
            List<ColumnSchemaResponse> columns = getColumns(targetSchema, tableName, primaryKeys);
            List<ForeignKeyResponse> foreignKeys = getForeignKeys(targetSchema, tableName);
            Long estimatedRows = getEstimatedRowCount(targetSchema, tableName);

            tables.add(TableSchemaResponse.of(tableName, targetSchema, estimatedRows, columns, foreignKeys));
        }

        log.info("Completed schema introspection: found {} tables", tables.size());
        return SchemaResponse.of(databaseName, targetSchema, tables);
    }

    private List<String> getTableNames(String schemaName) {
        String sql = """
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = ?
              AND table_type = 'BASE TABLE'
            ORDER BY table_name;
            """;
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("table_name"), schemaName);
        } catch (DataAccessException ex) {
            log.error("Error querying table names for schema {}: {}", schemaName, ex.getMessage());
            throw ex;
        }
    }

    private Set<String> getPrimaryKeys(String schemaName, String tableName) {
        String sql = """
            SELECT kcu.column_name
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
              ON tc.constraint_name = kcu.constraint_name
              AND tc.table_schema = kcu.table_schema
            WHERE tc.constraint_type = 'PRIMARY KEY'
              AND tc.table_schema = ?
              AND tc.table_name = ?;
            """;
        try {
            List<String> pkList = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("column_name"), schemaName, tableName);
            return new HashSet<>(pkList);
        } catch (Exception ex) {
            log.warn("Could not retrieve primary keys for {}.{}: {}", schemaName, tableName, ex.getMessage());
            return Collections.emptySet();
        }
    }

    private List<ColumnSchemaResponse> getColumns(String schemaName, String tableName, Set<String> primaryKeys) {
        String sql = """
            SELECT
                column_name,
                data_type,
                is_nullable,
                column_default,
                ordinal_position
            FROM information_schema.columns
            WHERE table_schema = ?
              AND table_name = ?
            ORDER BY ordinal_position;
            """;
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                String columnName = rs.getString("column_name");
                String dataType = rs.getString("data_type");
                boolean nullable = "YES".equalsIgnoreCase(rs.getString("is_nullable"));
                boolean isPk = primaryKeys.contains(columnName);
                String defaultValue = rs.getString("column_default");
                int ordinal = rs.getInt("ordinal_position");

                return ColumnSchemaResponse.of(columnName, dataType, nullable, isPk, defaultValue, ordinal);
            }, schemaName, tableName);
        } catch (DataAccessException ex) {
            log.error("Error querying columns for {}.{}: {}", schemaName, tableName, ex.getMessage());
            throw ex;
        }
    }

    private List<ForeignKeyResponse> getForeignKeys(String schemaName, String tableName) {
        String sql = """
            SELECT
                kcu.column_name AS column_name,
                ccu.table_name AS foreign_table_name,
                ccu.column_name AS foreign_column_name,
                tc.constraint_name AS constraint_name
            FROM information_schema.table_constraints AS tc
            JOIN information_schema.key_column_usage AS kcu
              ON tc.constraint_name = kcu.constraint_name
              AND tc.table_schema = kcu.table_schema
            JOIN information_schema.constraint_column_usage AS ccu
              ON ccu.constraint_name = tc.constraint_name
              AND ccu.table_schema = tc.table_schema
            WHERE tc.constraint_type = 'FOREIGN KEY'
              AND tc.table_schema = ?
              AND tc.table_name = ?
            ORDER BY kcu.column_name;
            """;
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> ForeignKeyResponse.of(
                    rs.getString("column_name"),
                    rs.getString("foreign_table_name"),
                    rs.getString("foreign_column_name"),
                    rs.getString("constraint_name")
            ), schemaName, tableName);
        } catch (Exception ex) {
            log.warn("Could not retrieve foreign keys for {}.{}: {}", schemaName, tableName, ex.getMessage());
            return Collections.emptyList();
        }
    }

    private Long getEstimatedRowCount(String schemaName, String tableName) {
        String sql = """
            SELECT COALESCE(c.reltuples, 0)::BIGINT AS estimate
            FROM pg_class c
            JOIN pg_namespace n ON n.oid = c.relnamespace
            WHERE n.nspname = ?
              AND c.relname = ?;
            """;
        try {
            List<Long> results = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("estimate"), schemaName, tableName);
            return results.isEmpty() ? 0L : Math.max(0L, results.get(0));
        } catch (Exception ex) {
            return 0L;
        }
    }

    /**
     * Formats schema into compact DDL text context for AI prompt injection (Phase 4).
     */
    public String formatSchemaForAiPrompt(SchemaResponse schemaResponse) {
        if (schemaResponse == null || schemaResponse.tables().isEmpty()) {
            return "-- No tables available";
        }
        StringBuilder sb = new StringBuilder();
        for (TableSchemaResponse table : schemaResponse.tables()) {
            sb.append("TABLE ").append(table.name()).append(" (\n");
            for (int i = 0; i < table.columns().size(); i++) {
                ColumnSchemaResponse col = table.columns().get(i);
                sb.append("  ").append(col.name()).append(" ").append(col.dataType());
                if (col.primaryKey()) {
                    sb.append(" PRIMARY KEY");
                }
                if (!col.nullable()) {
                    sb.append(" NOT NULL");
                }
                if (i < table.columns().size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            if (!table.foreignKeys().isEmpty()) {
                sb.append("  -- Foreign Keys:\n");
                for (ForeignKeyResponse fk : table.foreignKeys()) {
                    sb.append("  -- FOREIGN KEY (").append(fk.column())
                      .append(") REFERENCES ").append(fk.referencedTable())
                      .append("(").append(fk.referencedColumn()).append(")\n");
                }
            }
            sb.append(");\n\n");
        }
        return sb.toString().trim();
    }
}
