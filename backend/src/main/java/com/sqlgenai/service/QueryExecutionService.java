package com.sqlgenai.service;

import com.sqlgenai.config.DatabaseProperties;
import com.sqlgenai.dto.response.QueryResponse;
import com.sqlgenai.exception.SqlExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryExecutionService {
    private static final Logger log = LoggerFactory.getLogger(QueryExecutionService.class);

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseProperties databaseProperties;

    public QueryExecutionService(JdbcTemplate jdbcTemplate, DatabaseProperties databaseProperties) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseProperties = databaseProperties;
    }

    public QueryResponse executeQuery(String sql) {
        return executeQuery(sql, "SELECT", true);
    }

    public QueryResponse executeQuery(String sql, String statementType, boolean readOnly) {
        log.info("Executing SQL (type={}): {}", statementType, sql);
        long startTime = System.currentTimeMillis();

        try {
            jdbcTemplate.setQueryTimeout(databaseProperties.getSecurity().getQueryTimeoutSeconds());

            if (readOnly || "SELECT".equalsIgnoreCase(statementType)) {
                jdbcTemplate.setMaxRows(databaseProperties.getSecurity().getMaxRows());

                List<String> columns = new ArrayList<>();
                List<Map<String, Object>> rows = jdbcTemplate.query(sql, rs -> {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        columns.add(metaData.getColumnName(i));
                    }

                    List<Map<String, Object>> resultList = new ArrayList<>();
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(columns.get(i - 1), rs.getObject(i));
                        }
                        resultList.add(row);
                    }
                    return resultList;
                });

                long executionTime = System.currentTimeMillis() - startTime;
                log.info("Query executed successfully in {} ms, returned {} rows", executionTime, rows.size());
                return QueryResponse.success(sql, columns, rows, executionTime, statementType, true);
            } else {
                int rowsAffected = jdbcTemplate.update(sql);
                long executionTime = System.currentTimeMillis() - startTime;
                log.info("Statement ({}) executed successfully in {} ms, affected {} rows", statementType, executionTime, rowsAffected);
                return QueryResponse.dmlSuccess(sql, statementType, rowsAffected, executionTime);
            }
        } catch (DataAccessException e) {
            log.error("Query execution failed: {}", e.getMessage());
            throw new SqlExecutionException(
                    "Failed to execute SQL: " + e.getMessage(), e, isRepairableSqlError(e));
        }
    }

    private boolean isRepairableSqlError(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof SQLException sqlException) {
                String sqlState = sqlException.getSQLState();
                return sqlState != null && (sqlState.startsWith("42") || sqlState.startsWith("22"));
            }
            current = current.getCause();
        }
        return false;
    }
}