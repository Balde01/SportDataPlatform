package com.sportdataauth.util;

public interface TransactionRunner {
    <T> T runInTransaction(TransactionWork<T> work);
}