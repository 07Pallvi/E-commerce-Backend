package com.ecommerce.app.enums;

public enum Permission {
    READ("READ"),
    WRITE("WRITE"),
    DELETE("DELETE"),
    UPDATE("UPDATE");

    private final String description;

    Permission(String description) {
        this.description = description;
    }
    
    public String getName() {
        return description;
    }
}
