package com.example.springfirebaselogin.filter;

public enum UserTypes {
    USER("user"),
    SELLER("seller"),
    ANONYMOUS("anonymous");

    private final String value;

    private UserTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
