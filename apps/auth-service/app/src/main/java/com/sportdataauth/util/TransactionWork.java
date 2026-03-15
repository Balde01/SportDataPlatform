package com.sportdataauth.util;

@FunctionalInterface
public interface TransactionWork<T> {
    T run() throws Exception;

}
