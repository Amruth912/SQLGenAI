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
        assertDoesNotThrow(() -> sqlValidationService.validateSql("SELECT * FROM users"));
    }

    @Test
    void validateSql_update_throwsException() {
        assertThrows(SqlValidationException.class, () -> sqlValidationService.validateSql("UPDATE users SET name = 'test'"));
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
