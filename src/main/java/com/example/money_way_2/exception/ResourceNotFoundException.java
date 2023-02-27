package com.example.money_way_2.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException() {

    }

    public ResourceNotFoundException(String message) {super(message);}
}
