package com.sqlgenai.exception;

public class SqlValidationException extends RuntimeException {
    public SqlValidationException(String message) {
        super(message);
    }
}
