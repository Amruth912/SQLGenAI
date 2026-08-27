package com.sqlgenai.service;

/**
 * Result of SQL AST security analysis and classification.
 */
public record SqlValidationResult(
        String statementType,
        boolean readOnly,
        boolean requiresConfirmation,
        String riskLevel
) {
    public static SqlValidationResult select() {
        return new SqlValidationResult("SELECT", true, false, "READ_ONLY");
    }

    public static SqlValidationResult mutating(String type, String riskLevel) {
        return new SqlValidationResult(type, false, true, riskLevel);
    }
}
