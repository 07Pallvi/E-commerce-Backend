package com.ecommerce.app.enums;

public enum RoleName {

    ADMIN("ADMIN"),
    SELLER("SELLER"),
    CUSTOMER("CUSTOMER");

    private final String name;

    RoleName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
