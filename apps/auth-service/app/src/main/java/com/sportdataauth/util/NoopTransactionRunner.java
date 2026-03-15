package com.sportdataauth.util;

public class NoopTransactionRunner implements TransactionRunner {

    @Override
    public <T> T runInTransaction(TransactionWork<T> work) {
        try {
            return work.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Transaction work failed", e);
        }
    }
}