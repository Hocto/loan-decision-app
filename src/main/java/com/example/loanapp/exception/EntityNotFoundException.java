package com.example.loanapp.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends LoanException {

    private final String entity;

    public EntityNotFoundException(String message, String entity) {
        super(message);
        this.entity = entity;
    }
}
