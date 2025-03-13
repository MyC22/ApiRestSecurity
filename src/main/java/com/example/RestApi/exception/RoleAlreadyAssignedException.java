package com.example.RestApi.exception;

public class RoleAlreadyAssignedException extends RuntimeException{

    public RoleAlreadyAssignedException( String message ){
        super(message);
    }
}
