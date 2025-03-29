package com.mainapp.enums;
/**
 * Enum representing the different statuses an employee can have.
 * Author:Sidharth Guleria
 */
public enum Status {
    ACTIVE,
    INACTIVE;
    public static Status fromString(String status) {
        return Status.valueOf(status.toUpperCase());
    }
}