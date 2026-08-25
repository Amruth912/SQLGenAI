package com.sqlgenai.service;

import com.sqlgenai.config.DatabaseProperties;
import com.sqlgenai.dto.response.DatabaseHealthStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseHealthServiceTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    private DatabaseProperties properties;
    private DatabaseHealthService healthService;

    @BeforeEach
    void setUp() {
        properties = new DatabaseProperties();
        properties.setHost("localhost");
        properties.setPort(5432);
        properties.setDatabase("sqlgenai_db");
        properties.setUsername("postgres");
        properties.setPassword("postgres");

        healthService = new DatabaseHealthService(dataSource, properties);
    }

    @Test
    @DisplayName("checkDatabaseConnectivity returns connected=true when connection is valid")
    void testCheckDatabaseConnectivity_Success() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);

        DatabaseHealthStatus status = healthService.checkDatabaseConnectivity();

        assertThat(status.connected()).isTrue();
        assertThat(status.databaseName()).isEqualTo("sqlgenai_db");
        assertThat(status.host()).isEqualTo("localhost");
        assertThat(status.port()).isEqualTo(5432);
        assertThat(status.latencyMs()).isNotNull();
        assertThat(status.message()).contains("Successfully connected");

        verify(connection).close();
    }

    @Test
    @DisplayName("checkDatabaseConnectivity returns connected=false when connection.isValid is false")
    void testCheckDatabaseConnectivity_InvalidConnection() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(false);

        DatabaseHealthStatus status = healthService.checkDatabaseConnectivity();

        assertThat(status.connected()).isFalse();
        assertThat(status.databaseName()).isEqualTo("sqlgenai_db");
        assertThat(status.latencyMs()).isNull();
        assertThat(status.message()).contains("validation check failed");

        verify(connection).close();
    }

    @Test
    @DisplayName("checkDatabaseConnectivity handles SQLException gracefully without throwing exception")
    void testCheckDatabaseConnectivity_SQLException() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection refused"));

        DatabaseHealthStatus status = healthService.checkDatabaseConnectivity();

        assertThat(status.connected()).isFalse();
        assertThat(status.databaseName()).isEqualTo("sqlgenai_db");
        assertThat(status.latencyMs()).isNull();
        assertThat(status.message()).contains("Connection failed: Connection refused");
    }
}
