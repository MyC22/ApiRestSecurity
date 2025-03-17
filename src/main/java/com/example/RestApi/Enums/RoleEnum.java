package com.example.RestApi.Enums;

public enum RoleEnum {
    ADMIN("ADMIN"),
    USER("USER"),
    INVITED("INVITED"),
    DEVELOPER("DEVELOPER");

    public final String label;

    RoleEnum(String label) {
        this.label = label;
    }

    public static RoleEnum of(String label) {
        for (RoleEnum roleEnum : values()) {
            if (roleEnum.label.equalsIgnoreCase(label))
                return roleEnum;
        }
        return null;
    }

}
