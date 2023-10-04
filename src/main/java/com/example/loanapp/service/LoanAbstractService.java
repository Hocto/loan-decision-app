package com.example.loanapp.service;

public interface LoanAbstractService<T, R> {

    R run(T t);
}
