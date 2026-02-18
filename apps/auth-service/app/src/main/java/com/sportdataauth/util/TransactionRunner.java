package com.sportdataauth.util;

public interface TransactionRunner {
        void runInTransaction(Runnable action);
    }
