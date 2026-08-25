package com.sqlgenai.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private final DatabaseProperties databaseProperties;

    public DatabaseConfig(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(databaseProperties.getJdbcUrl());
        config.setUsername(databaseProperties.getUsername());
        config.setPassword(databaseProperties.getPassword());

        // Connection Pool Tuning (Defaults & Graceful Timeouts)
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(5000); // 5 seconds timeout for fail-fast
        config.setValidationTimeout(3000); // 3 seconds validation timeout
        config.setIdleTimeout(600000);      // 10 minutes
        config.setMaxLifetime(1800000);     // 30 minutes
        config.setPoolName("SQLGenAI-HikariPool");

        // Fail-fast on startup disabled so app starts even if PostgreSQL is momentarily restarting
        config.setInitializationFailTimeout(-1);

        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
