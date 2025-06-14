package com.mycompany.mavenproject3.event;

public class DataChangeEvent {
    private final String operation;

    public DataChangeEvent(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
