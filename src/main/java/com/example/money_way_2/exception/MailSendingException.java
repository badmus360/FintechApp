package com.example.money_way_2.exception;

public class MailSendingException extends RuntimeException{

    public MailSendingException(String errorMessage) { super(errorMessage); }
}
