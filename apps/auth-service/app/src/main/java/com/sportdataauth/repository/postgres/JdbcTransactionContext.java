package com.sportdataauth.repository.postgres;

import java.sql.Connection;

public final class JdbcTransactionContext {
    private static final ThreadLocal<Connection> CURRENT = new ThreadLocal<>();

    private JdbcTransactionContext() {}

    public static Connection getCurrent() {
        return CURRENT.get();
    }

    public static Connection requireCurrent() {
        Connection conn = CURRENT.get();
        if (conn == null) {
            throw new IllegalStateException("No active JDBC transaction");
        }
        return conn;
    }

    public static boolean hasCurrent() {
        return CURRENT.get() != null;
    }

    static void bind(Connection conn) {
        CURRENT.set(conn);
    }

    static void unbind() {
        CURRENT.remove();
    }
}