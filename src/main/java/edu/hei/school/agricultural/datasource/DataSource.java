package edu.hei.school.agricultural.datasource;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DataSource {

    private final Dotenv dotenv = Dotenv.load();

    @Bean
    public Connection getConnection() {
        try {
            String jdbcUrl = dotenv.get("DB_URL");
            String user = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            return DriverManager.getConnection(jdbcUrl, user, password);

        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }
}