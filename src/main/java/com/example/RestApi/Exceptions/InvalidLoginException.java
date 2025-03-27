package com.example.RestApi.Exceptions;

public class InvalidLoginException extends RuntimeException{

    public InvalidLoginException(String message){
        super(message);
    }
}
