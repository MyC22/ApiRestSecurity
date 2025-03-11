package com.example.RestApi.Exceptions;

public class RoleNotAssignedException extends RuntimeException{

    public RoleNotAssignedException( String message ){
        super(message);
    }
}
