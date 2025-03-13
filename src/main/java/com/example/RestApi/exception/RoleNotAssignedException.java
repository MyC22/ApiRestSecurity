package com.example.RestApi.exception;

public class RoleNotAssignedException extends RuntimeException{

    public RoleNotAssignedException( String message ){
        super(message);
    }
}
