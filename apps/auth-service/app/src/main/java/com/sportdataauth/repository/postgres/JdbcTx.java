package com.sportdataauth.repository.postgres;

import java.sql.Connection;
import java.sql.SQLException;

public final class JdbcTx {
    private JdbcTx() {}

    public interface Work<T> { T run(Connection conn) throws Exception; }

    public static <T> T inTx(Work<T> work) {
        try (Connection conn = JdbcConnectionFactory.open()) {
            boolean oldAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                T result = work.run(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
            } finally {
                conn.setAutoCommit(oldAuto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB connection/tx error", e);
        }
    }
}