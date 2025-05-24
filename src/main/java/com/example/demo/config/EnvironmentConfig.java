package com.example.demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class EnvironmentConfig {

    static {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("db_url", Objects.requireNonNull(dotenv.get("DB_URL")));
        System.setProperty("db_user", Objects.requireNonNull(dotenv.get("DB_USER")));
        System.setProperty("db_password", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));
        System.setProperty("API_KEY", Objects.requireNonNull(dotenv.get("API_KEY")));
    }
}
