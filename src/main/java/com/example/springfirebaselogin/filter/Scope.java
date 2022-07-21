package com.example.springfirebaselogin.filter;

public enum Scope {
    USER("USER"),
    SELLER("SELLER"),
    ANONYMOUS("ANONYMOUS");

    private String value;

    public String getValue() {
        return value;
    }

    Scope(String value) {
        this.value = value;
    }
}
