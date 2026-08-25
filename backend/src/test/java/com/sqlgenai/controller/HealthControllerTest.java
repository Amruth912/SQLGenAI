package com.sqlgenai.controller;

import com.sqlgenai.dto.response.DatabaseHealthStatus;
import com.sqlgenai.service.DatabaseHealthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatabaseHealthService databaseHealthService;

    @Test
    @DisplayName("GET /api/v1/health should return UP status when database is connected")
    void testGetHealth_WhenDatabaseConnected() throws Exception {
        when(databaseHealthService.checkDatabaseConnectivity())
                .thenReturn(DatabaseHealthStatus.connected("sqlgenai_db", "localhost", 5432, 5L));

        mockMvc.perform(get("/api/v1/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("sqlgenai-backend"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.database.connected").value(true))
                .andExpect(jsonPath("$.database.databaseName").value("sqlgenai_db"))
                .andExpect(jsonPath("$.database.host").value("localhost"))
                .andExpect(jsonPath("$.database.port").value(5432))
                .andExpect(jsonPath("$.database.latencyMs").value(5))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /api/v1/health should return DEGRADED status when database is disconnected")
    void testGetHealth_WhenDatabaseDisconnected() throws Exception {
        when(databaseHealthService.checkDatabaseConnectivity())
                .thenReturn(DatabaseHealthStatus.disconnected("sqlgenai_db", "localhost", 5432, "Connection refused"));

        mockMvc.perform(get("/api/v1/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("DEGRADED"))
                .andExpect(jsonPath("$.database.connected").value(false))
                .andExpect(jsonPath("$.database.message").value("Connection refused"));
    }
}
