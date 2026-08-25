package com.sqlgenai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final DataSource dataSource;

    @Value("${app.datasource.init-sample-data:false}")
    private boolean initSampleData;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        if (initSampleData) {
            log.info("Initializing sample PostgreSQL schema and dataset (app.datasource.init-sample-data=true)...");
            initializeSampleDatabase();
        }
    }

    public boolean initializeSampleDatabase() {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/schema.sql"));
            populator.addScript(new ClassPathResource("db/data.sql"));
            populator.setContinueOnError(false);
            populator.execute(dataSource);
            log.info("Successfully initialized PostgreSQL sample database (departments, employees, projects, employee_projects, salaries_history).");
            return true;
        } catch (Exception ex) {
            log.warn("Sample database initialization skipped or encountered error: {}", ex.getMessage());
            return false;
        }
    }
}
