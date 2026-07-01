package com.example.orderservice.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) { super(message);}
}
