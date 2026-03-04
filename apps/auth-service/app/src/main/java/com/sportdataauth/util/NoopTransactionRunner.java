package com.sportdataauth.util;

public class NoopTransactionRunner implements TransactionRunner {

    //NOOP (No-Operation) for spring in future
    @Override
    public void runInTransaction(Runnable action) {
        action.run();
    }

}
