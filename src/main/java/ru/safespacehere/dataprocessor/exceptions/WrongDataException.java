package ru.safespacehere.dataprocessor.exceptions;

public class WrongDataException extends RuntimeException{
    public WrongDataException(String message){
        super(message);
    }
}
