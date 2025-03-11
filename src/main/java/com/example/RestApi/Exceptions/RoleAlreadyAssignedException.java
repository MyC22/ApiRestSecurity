package com.example.RestApi.Exceptions;

public class RoleAlreadyAssignedException extends RuntimeException{

    public RoleAlreadyAssignedException( String message ){
        super(message);
    }
}
