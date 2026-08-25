package com.sqlgenai.controller;

import com.sqlgenai.dto.request.QueryRequest;
import com.sqlgenai.dto.response.QueryResponse;
import com.sqlgenai.exception.SqlExecutionException;
import com.sqlgenai.service.QueryExecutionService;
import com.sqlgenai.service.SqlValidationService;
import com.sqlgenai.service.ai.AiSqlGenerationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class QueryController {

    private final AiSqlGenerationService aiSqlGenerationService;
    private final SqlValidationService sqlValidationService;
    private final QueryExecutionService queryExecutionService;

    public QueryController(AiSqlGenerationService aiSqlGenerationService,
                           SqlValidationService sqlValidationService,
                           QueryExecutionService queryExecutionService) {
        this.aiSqlGenerationService = aiSqlGenerationService;
        this.sqlValidationService = sqlValidationService;
        this.queryExecutionService = queryExecutionService;
    }

    @PostMapping("/query")
    public ResponseEntity<QueryResponse> processQuery(@Valid @RequestBody QueryRequest request) {
        String generatedSql = aiSqlGenerationService.generateSql(request.question(), request.schemaName());
        sqlValidationService.validateSql(generatedSql);

        try {
            return ResponseEntity.ok(queryExecutionService.executeQuery(generatedSql));
        } catch (SqlExecutionException executionError) {
            if (!executionError.isRepairable()) {
                throw executionError;
            }
            // One bounded retry fixes schema-semantic mistakes while retaining validation and execution limits.
            String repairedSql = aiSqlGenerationService.repairSql(
                    request.question(), request.schemaName(), generatedSql, executionError.getMessage());
            sqlValidationService.validateSql(repairedSql);
            return ResponseEntity.ok(queryExecutionService.executeQuery(repairedSql));
        }
    }
}