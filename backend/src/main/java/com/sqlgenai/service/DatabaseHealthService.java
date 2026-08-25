package com.sqlgenai.service;

import com.sqlgenai.config.DatabaseProperties;
import com.sqlgenai.dto.response.DatabaseHealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class DatabaseHealthService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseHealthService.class);

    private final DataSource dataSource;
    private final DatabaseProperties databaseProperties;

    public DatabaseHealthService(DataSource dataSource, DatabaseProperties databaseProperties) {
        this.dataSource = dataSource;
        this.databaseProperties = databaseProperties;
    }

    public DatabaseHealthStatus checkDatabaseConnectivity() {
        String host = databaseProperties.getHost();
        int port = databaseProperties.getPort();
        String dbName = databaseProperties.getDatabase();

        long startTime = System.currentTimeMillis();
        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(2);
            long latency = System.currentTimeMillis() - startTime;
            if (valid) {
                return DatabaseHealthStatus.connected(dbName, host, port, latency);
            } else {
                return DatabaseHealthStatus.disconnected(dbName, host, port, "Connection validation check failed");
            }
        } catch (SQLException ex) {
            log.warn("PostgreSQL connectivity check failed for {}@{}:{}: {}", dbName, host, port, ex.getMessage());
            return DatabaseHealthStatus.disconnected(dbName, host, port, "Connection failed: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error checking database health", ex);
            return DatabaseHealthStatus.disconnected(dbName, host, port, "Unexpected error: " + ex.getMessage());
        }
    }
}
