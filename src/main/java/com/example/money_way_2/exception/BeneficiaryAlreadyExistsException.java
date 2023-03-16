package com.example.money_way_2.exception;

public class BeneficiaryAlreadyExistsException extends RuntimeException {
    public BeneficiaryAlreadyExistsException (String message)  {
        super(message);
    };
}
