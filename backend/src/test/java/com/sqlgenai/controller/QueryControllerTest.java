package com.sqlgenai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqlgenai.dto.request.QueryRequest;
import com.sqlgenai.dto.response.QueryResponse;
import com.sqlgenai.exception.SqlExecutionException;
import com.sqlgenai.service.QueryExecutionService;
import com.sqlgenai.service.SqlValidationService;
import com.sqlgenai.service.ai.AiSqlGenerationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QueryController.class)
class QueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiSqlGenerationService aiSqlGenerationService;

    @MockBean
    private SqlValidationService sqlValidationService;

    @MockBean
    private QueryExecutionService queryExecutionService;

    @Test
    void processQuery_success() throws Exception {
        QueryRequest request = new QueryRequest("Show employees", null);
        String sql = "SELECT * FROM employees;";
        QueryResponse response = QueryResponse.success(sql, List.of("id"), List.of(Map.of("id", 1)), 10L);

        Mockito.when(aiSqlGenerationService.generateSql(anyString(), any())).thenReturn(sql);
        Mockito.when(queryExecutionService.executeQuery(sql)).thenReturn(response);

        mockMvc.perform(post("/api/v1/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedSql").value(sql))
                .andExpect(jsonPath("$.rowCount").value(1));
    }

    @Test
    void processQuery_repairsOnceAfterExecutionFailure() throws Exception {
        QueryRequest request = new QueryRequest("Show employees", "public");
        String failedSql = "SELECT missing_column FROM employees;";
        String repairedSql = "SELECT first_name FROM employees;";
        QueryResponse repairedResponse = QueryResponse.success(repairedSql, List.of("first_name"), List.of(Map.of("first_name", "Maya")), 8L);

        Mockito.when(aiSqlGenerationService.generateSql("Show employees", "public")).thenReturn(failedSql);
        Mockito.when(queryExecutionService.executeQuery(failedSql))
                .thenThrow(new SqlExecutionException("column missing_column does not exist"));
        Mockito.when(aiSqlGenerationService.repairSql(
                eq("Show employees"), eq("public"), eq(failedSql), anyString())).thenReturn(repairedSql);
        Mockito.when(queryExecutionService.executeQuery(repairedSql)).thenReturn(repairedResponse);

        mockMvc.perform(post("/api/v1/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedSql").value(repairedSql));

        verify(sqlValidationService).validateSql(failedSql);
        verify(sqlValidationService).validateSql(repairedSql);
        verify(aiSqlGenerationService).repairSql(eq("Show employees"), eq("public"), eq(failedSql), anyString());
    }
}