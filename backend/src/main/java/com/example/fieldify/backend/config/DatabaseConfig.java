package com.example.fieldify.backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;

public class DatabaseConfig {

    private final HikariDataSource dataSource;

    public DatabaseConfig(AppConfig config) {
        ensureDatabaseExists(config);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getDbUrl());
        hikariConfig.setUsername(config.getDbUser());
        hikariConfig.setPassword(config.getDbPassword());
        hikariConfig.setMaximumPoolSize(config.getDbPoolMaxSize());
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setConnectionTimeout(10000);

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    private void ensureDatabaseExists(AppConfig config) {
        String dbUrl = config.getDbUrl();
        int lastSlash = dbUrl.lastIndexOf('/');
        if (lastSlash == -1) return;
        String baseUrl = dbUrl.substring(0, lastSlash + 1) + "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            try (Connection conn = java.sql.DriverManager.getConnection(baseUrl, config.getDbUser(), config.getDbPassword());
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE \"Sportity\"");
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void initDatabase() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("db/schema.sql");
            if (inputStream != null) {
                String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                stmt.execute(sql);
            }
        } catch (Exception e) {
            System.err.println("Database initialization warning: " + e.getMessage());
        }
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
