package com.sqlgenai.controller;

import com.sqlgenai.dto.response.ColumnSchemaResponse;
import com.sqlgenai.dto.response.ForeignKeyResponse;
import com.sqlgenai.dto.response.SchemaResponse;
import com.sqlgenai.dto.response.TableSchemaResponse;
import com.sqlgenai.service.DatabaseHealthService;
import com.sqlgenai.service.SchemaIntrospectionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SchemaController.class)
class SchemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SchemaIntrospectionService schemaIntrospectionService;

    @MockBean
    private DatabaseHealthService databaseHealthService;

    @Test
    @DisplayName("GET /api/v1/schema returns HTTP 200 with schema metadata")
    void testGetSchema_Success() throws Exception {
        TableSchemaResponse table = TableSchemaResponse.of(
                "employees",
                "public",
                35L,
                List.of(
                        ColumnSchemaResponse.of("id", "bigint", false, true, null, 1),
                        ColumnSchemaResponse.of("department_id", "bigint", false, false, null, 2),
                        ColumnSchemaResponse.of("first_name", "character varying", false, false, null, 3),
                        ColumnSchemaResponse.of("salary", "numeric", true, false, null, 4)
                ),
                List.of(
                        ForeignKeyResponse.of("department_id", "departments", "id", "fk_dept")
                )
        );

        SchemaResponse schemaResponse = SchemaResponse.of("sqlgenai_db", "public", List.of(table));
        when(schemaIntrospectionService.introspectSchema(eq("public"))).thenReturn(schemaResponse);

        mockMvc.perform(get("/api/v1/schema")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.databaseName").value("sqlgenai_db"))
                .andExpect(jsonPath("$.schemaName").value("public"))
                .andExpect(jsonPath("$.tableCount").value(1))
                .andExpect(jsonPath("$.tables[0].name").value("employees"))
                .andExpect(jsonPath("$.tables[0].columns").isArray())
                .andExpect(jsonPath("$.tables[0].columns[0].name").value("id"))
                .andExpect(jsonPath("$.tables[0].columns[0].primaryKey").value(true))
                .andExpect(jsonPath("$.tables[0].foreignKeys[0].column").value("department_id"))
                .andExpect(jsonPath("$.tables[0].foreignKeys[0].referencedTable").value("departments"));
    }

    @Test
    @DisplayName("GET /api/v1/schema handles database failures gracefully with GlobalExceptionHandler")
    void testGetSchema_DatabaseFailure() throws Exception {
        when(schemaIntrospectionService.introspectSchema(eq("public")))
                .thenThrow(new DataAccessResourceFailureException("Failed to obtain JDBC Connection"));

        mockMvc.perform(get("/api/v1/schema")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("DATABASE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value(containsString("Failed to obtain JDBC Connection")));
    }
}
