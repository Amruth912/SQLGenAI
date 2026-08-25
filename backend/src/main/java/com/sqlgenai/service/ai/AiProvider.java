package com.sqlgenai.service.ai;

public interface AiProvider {
    String generateSql(String schemaContext, String question);

    String repairSql(String schemaContext, String question, String failedSql, String databaseError);
}