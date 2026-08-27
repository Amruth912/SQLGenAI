package com.sqlgenai.service;

import com.sqlgenai.exception.SqlValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SqlValidationServiceTest {

    private SqlValidationService sqlValidationService;

    @BeforeEach
    void setUp() {
        sqlValidationService = new SqlValidationService();
    }

    @Test
    void validateSql_validSelect_success() {
        SqlValidationResult result = sqlValidationService.validateAndClassify("SELECT * FROM users");
        org.junit.jupiter.api.Assertions.assertTrue(result.readOnly());
        org.junit.jupiter.api.Assertions.assertEquals("SELECT", result.statementType());
    }

    @Test
    void validateSql_validInsert_success() {
        SqlValidationResult result = sqlValidationService.validateAndClassify("INSERT INTO users (id, name) VALUES (1, 'Alice')");
        org.junit.jupiter.api.Assertions.assertFalse(result.readOnly());
        org.junit.jupiter.api.Assertions.assertEquals("INSERT", result.statementType());
        org.junit.jupiter.api.Assertions.assertTrue(result.requiresConfirmation());
    }

    @Test
    void validateSql_validUpdate_success() {
        SqlValidationResult result = sqlValidationService.validateAndClassify("UPDATE users SET name = 'test' WHERE id = 1");
        org.junit.jupiter.api.Assertions.assertFalse(result.readOnly());
        org.junit.jupiter.api.Assertions.assertEquals("UPDATE", result.statementType());
        org.junit.jupiter.api.Assertions.assertTrue(result.requiresConfirmation());
    }

    @Test
    void validateSql_validCreateTable_success() {
        SqlValidationResult result = sqlValidationService.validateAndClassify("CREATE TABLE students (id BIGINT PRIMARY KEY, name VARCHAR(100), age INT)");
        org.junit.jupiter.api.Assertions.assertFalse(result.readOnly());
        org.junit.jupiter.api.Assertions.assertEquals("CREATE_TABLE", result.statementType());
    }

    @Test
    void validateSql_validDropTable_success() {
        SqlValidationResult result = sqlValidationService.validateAndClassify("DROP TABLE students");
        org.junit.jupiter.api.Assertions.assertFalse(result.readOnly());
        org.junit.jupiter.api.Assertions.assertEquals("DROP", result.statementType());
        org.junit.jupiter.api.Assertions.assertEquals("DESTRUCTIVE", result.riskLevel());
    }

    @Test
    void validateSql_multipleStatements_throwsException() {
        assertThrows(SqlValidationException.class, () -> sqlValidationService.validateSql("SELECT * FROM users; DROP TABLE users;"));
    }
    
    @Test
    void validateSql_empty_throwsException() {
        assertThrows(SqlValidationException.class, () -> sqlValidationService.validateSql(""));
    }
}
