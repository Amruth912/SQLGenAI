package com.sqlgenai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.datasource")
public class DatabaseProperties {

    private String host = "localhost";
    private int port = 5432;
    private String database = "sqlgenai_db";
    private String username = "postgres";
    private String password = "postgres";
    private String jdbcUrl;
    private Security security = new Security();

    public String getJdbcUrl() {
        if (jdbcUrl != null && !jdbcUrl.isBlank()) {
            return jdbcUrl;
        }
        return String.format("jdbc:postgresql://%s:%d/%s?sslmode=prefer", host, port, database);
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * SQL execution security limits (bound from app.security.* in application.yml via parent prefix).
     * Note: these are stored inside DatabaseProperties for convenience but are read
     * from the same @ConfigurationProperties binding. The YAML keys are under app.datasource.security.*
     * We also expose a separate AppSecurityProperties bean for the app.security.* namespace.
     */
    public static class Security {
        private int queryTimeoutSeconds = 5;
        private int maxRows = 500;

        public int getQueryTimeoutSeconds() {
            return queryTimeoutSeconds;
        }

        public void setQueryTimeoutSeconds(int queryTimeoutSeconds) {
            this.queryTimeoutSeconds = queryTimeoutSeconds;
        }

        public int getMaxRows() {
            return maxRows;
        }

        public void setMaxRows(int maxRows) {
            this.maxRows = maxRows;
        }
    }
}
