package com.vortex.todobackup.domain;

public enum BackupStatus {

    IN_PROGRESS("In progress"),
    OK("OK"),
    FAILED("Failed");

    private String name;

    BackupStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
