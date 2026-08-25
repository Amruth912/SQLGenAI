package com.sqlgenai;

import com.sqlgenai.service.DatabaseHealthService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SqlGenAiApplicationTests {

    @MockBean
    private DatabaseHealthService databaseHealthService;

    @Test
    void contextLoads() {
    }
}
