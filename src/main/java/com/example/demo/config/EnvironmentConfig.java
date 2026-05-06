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
        System.setProperty("jwt_secret", Objects.requireNonNull(dotenv.get("JWT_SECRET")));
        System.setProperty("access_token", Objects.requireNonNull(dotenv.get("PROD_ACCESS_TOKEN")));
        System.setProperty("CLOUD_NAME", Objects.requireNonNull(dotenv.get("CLOUD_NAME")));
        System.setProperty("CLOUD_API_KEY", Objects.requireNonNull(dotenv.get("CLOUD_API_KEY")));
        System.setProperty("CLOUD_API_SECRET", Objects.requireNonNull(dotenv.get("CLOUD_API_SECRET")));
        System.setProperty("EMAIL", Objects.requireNonNull(dotenv.get("EMAIL")));
        System.setProperty("EMAIL_PASSWORD", Objects.requireNonNull(dotenv.get("EMAIL_PASSWORD")));
    }
}
