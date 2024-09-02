package ru.safespacehere.dataprocessor.exceptions;

public class NotExpectedTokenException extends RuntimeException{
    public NotExpectedTokenException(String message){
        super(message);
    }
}
