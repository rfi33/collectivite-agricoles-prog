package com.collectivity.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DataSource {

    private static final Dotenv dotenv = Dotenv.load();

    private final String jdbcUrl  = dotenv.get("JDBC_URL");
    private final String user     = dotenv.get("DB_USER");
    private final String password = dotenv.get("DB_PASSWORD");

    @Bean
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }
}