package com.sqlgenai.service;

import com.sqlgenai.config.DatabaseProperties;
import com.sqlgenai.dto.response.ColumnSchemaResponse;
import com.sqlgenai.dto.response.ForeignKeyResponse;
import com.sqlgenai.dto.response.SchemaResponse;
import com.sqlgenai.dto.response.TableSchemaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchemaIntrospectionServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private DatabaseProperties databaseProperties;
    private SchemaIntrospectionService schemaService;

    @BeforeEach
    void setUp() {
        databaseProperties = new DatabaseProperties();
        databaseProperties.setDatabase("sqlgenai_db");
        databaseProperties.setHost("localhost");
        databaseProperties.setPort(5432);

        schemaService = new SchemaIntrospectionService(jdbcTemplate, databaseProperties);
    }

    @Test
    @DisplayName("introspectSchema successfully discovers tables, columns, primary keys, and foreign keys")
    @SuppressWarnings("unchecked")
    void testIntrospectSchema_Success() {
        // Mock table list query
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("public")))
                .thenReturn(List.of("departments", "employees"));

        // Mock PK query for departments and employees
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("public"), eq("departments")))
                .thenReturn(List.of("id")) // PK
                .thenReturn(List.of(ColumnSchemaResponse.of("id", "bigint", false, true, null, 1),
                                    ColumnSchemaResponse.of("name", "character varying", false, false, null, 2))) // Columns
                .thenReturn(Collections.emptyList()) // FKs
                .thenReturn(List.of(8L)); // Row estimate

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("public"), eq("employees")))
                .thenReturn(List.of("id")) // PK
                .thenReturn(List.of(ColumnSchemaResponse.of("id", "bigint", false, true, null, 1),
                                    ColumnSchemaResponse.of("department_id", "bigint", false, false, null, 2),
                                    ColumnSchemaResponse.of("first_name", "character varying", false, false, null, 3))) // Columns
                .thenReturn(List.of(ForeignKeyResponse.of("department_id", "departments", "id", "fk_dept"))) // FKs
                .thenReturn(List.of(35L)); // Row estimate

        SchemaResponse response = schemaService.introspectSchema("public");

        assertThat(response).isNotNull();
        assertThat(response.databaseName()).isEqualTo("sqlgenai_db");
        assertThat(response.schemaName()).isEqualTo("public");
        assertThat(response.tableCount()).isEqualTo(2);
        assertThat(response.tables()).hasSize(2);

        TableSchemaResponse deptTable = response.tables().get(0);
        assertThat(deptTable.name()).isEqualTo("departments");
        assertThat(deptTable.columns()).hasSize(2);

        TableSchemaResponse empTable = response.tables().get(1);
        assertThat(empTable.name()).isEqualTo("employees");
        assertThat(empTable.foreignKeys()).hasSize(1);
        assertThat(empTable.foreignKeys().get(0).referencedTable()).isEqualTo("departments");
    }

    @Test
    @DisplayName("introspectSchema handles empty schema gracefully")
    @SuppressWarnings("unchecked")
    void testIntrospectSchema_EmptySchema() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("public")))
                .thenReturn(Collections.emptyList());

        SchemaResponse response = schemaService.introspectSchema("public");

        assertThat(response).isNotNull();
        assertThat(response.tableCount()).isEqualTo(0);
        assertThat(response.tables()).isEmpty();
    }

    @Test
    @DisplayName("formatSchemaForAiPrompt formats schema into readable DDL string")
    void testFormatSchemaForAiPrompt() {
        TableSchemaResponse table = TableSchemaResponse.of(
                "departments",
                "public",
                8L,
                List.of(
                        ColumnSchemaResponse.of("id", "bigint", false, true, null, 1),
                        ColumnSchemaResponse.of("name", "character varying", false, false, null, 2)
                ),
                List.of()
        );

        SchemaResponse response = SchemaResponse.of("sqlgenai_db", "public", List.of(table));
        String ddl = schemaService.formatSchemaForAiPrompt(response);

        assertThat(ddl).contains("TABLE departments (");
        assertThat(ddl).contains("id bigint PRIMARY KEY NOT NULL");
        assertThat(ddl).contains("name character varying NOT NULL");
    }
}
