package com.sqlgenai.service.ai;

import com.sqlgenai.dto.response.SchemaResponse;
import com.sqlgenai.exception.AiGenerationException;
import com.sqlgenai.service.SchemaIntrospectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AiSqlGenerationService {
    private static final Logger log = LoggerFactory.getLogger(AiSqlGenerationService.class);

    private final SchemaIntrospectionService schemaService;
    private final AiProvider aiProvider;

    public AiSqlGenerationService(SchemaIntrospectionService schemaService, AiProvider aiProvider) {
        this.schemaService = schemaService;
        this.aiProvider = aiProvider;
    }

    public String generateSql(String question, String schemaName) {
        try {
            log.info("Generating SQL for request");
            return cleanSql(aiProvider.generateSql(schemaContext(schemaName), question));
        } catch (Exception e) {
            log.error("Failed to generate SQL", e);
            throw new AiGenerationException("Failed to generate SQL: " + e.getMessage(), e);
        }
    }

    /** Repairs a generated query once; the controller still validates before executing it. */
    public String repairSql(String question, String schemaName, String failedSql, String databaseError) {
        try {
            log.info("Repairing generated SQL after database execution failure");
            return cleanSql(aiProvider.repairSql(schemaContext(schemaName), question, failedSql, databaseError));
        } catch (Exception e) {
            log.error("Failed to repair SQL", e);
            throw new AiGenerationException("Failed to repair SQL: " + e.getMessage(), e);
        }
    }

    private String schemaContext(String schemaName) {
        SchemaResponse schema = schemaService.introspectSchema(schemaName);
        return schemaService.formatSchemaForAiPrompt(schema);
    }

    private String cleanSql(String sql) {
        if (sql == null || sql.isBlank()) return "";
        String cleaned = sql.trim();
        // Extract content inside ```sql ... ``` or ``` ... ``` if present
        if (cleaned.contains("```")) {
            int start = cleaned.indexOf("```");
            int firstNewline = cleaned.indexOf('\n', start);
            int end = cleaned.lastIndexOf("```");
            if (firstNewline != -1 && end > firstNewline) {
                cleaned = cleaned.substring(firstNewline + 1, end).trim();
            } else if (end > start + 3) {
                cleaned = cleaned.substring(start + 3, end).trim();
            }
        }
        // Remove markdown backticks if wrapped like `SELECT ...`
        if (cleaned.startsWith("`") && cleaned.endsWith("`") && cleaned.length() > 2) {
            cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
        }
        return cleaned.trim();
    }
}