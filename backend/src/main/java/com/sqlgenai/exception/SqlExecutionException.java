package com.sqlgenai.exception;

public class SqlExecutionException extends RuntimeException {
    private final boolean repairable;

    public SqlExecutionException(String message) {
        this(message, null, true);
    }

    public SqlExecutionException(String message, Throwable cause) {
        this(message, cause, true);
    }

    public SqlExecutionException(String message, Throwable cause, boolean repairable) {
        super(message, cause);
        this.repairable = repairable;
    }

    public boolean isRepairable() {
        return repairable;
    }
}