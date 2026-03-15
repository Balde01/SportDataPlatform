package com.sportdataauth.repository.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class JdbcConnectionFactory {
    private JdbcConnectionFactory() {}

    public static Connection open() throws SQLException {
        String url = env("DB_URL");
        String user = env("DB_USER");
        String password = env("DB_PASSWORD");
        return DriverManager.getConnection(url, user, password);
    }

    private static String env(String key) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) throw new IllegalStateException("Missing env var: " + key);
        return v;
    }
}