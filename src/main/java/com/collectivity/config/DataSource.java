package com.collectivity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DataSource {

    private final String jdbcUrl  = System.getenv("JDBC_URL");
    private final String user     = System.getenv("DB_USER");
    private final String password = System.getenv("DB_PASSWORD");

    @Bean
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }
}