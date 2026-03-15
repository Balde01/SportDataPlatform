package com.sportdataauth.repository.postgres;

import java.sql.Connection;
import java.sql.SQLException;

import com.sportdataauth.util.TransactionRunner;
import com.sportdataauth.util.TransactionWork;

public class JdbcTransactionRunner implements TransactionRunner {

    @Override
    public <T> T runInTransaction(TransactionWork<T> work) {
        if (JdbcTransactionContext.hasCurrent()) {
            try {
                return work.run();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Transaction work failed", e);
            }
        }

        try (Connection conn = JdbcConnectionFactory.open()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            JdbcTransactionContext.bind(conn);

            try {
                T result = work.run();
                conn.commit();
                return result;
            } catch (RuntimeException e) {
                conn.rollback();
                throw e;
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Transaction failed", e);
            } finally {
                JdbcTransactionContext.unbind();
                conn.setAutoCommit(oldAutoCommit);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database transaction error", e);
        }
    }
}