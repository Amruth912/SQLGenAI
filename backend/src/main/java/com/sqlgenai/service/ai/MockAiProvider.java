package com.sqlgenai.service.ai;

/**
 * Kept for tests that choose to supply a deterministic provider explicitly.
 * It is not a Spring component and never returns example-specific SQL.
 */
public class MockAiProvider implements AiProvider {
    @Override
    public String generateSql(String schemaContext, String question) {
        throw new UnsupportedOperationException("Mock SQL generation is disabled. Configure an AI provider for schema-aware SQL generation.");
    }

    @Override
    public String repairSql(String schemaContext, String question, String failedSql, String databaseError) {
        throw new UnsupportedOperationException("Mock SQL repair is disabled. Configure an AI provider for schema-aware SQL generation.");
    }
}