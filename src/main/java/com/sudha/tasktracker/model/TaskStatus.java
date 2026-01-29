package com.sudha.tasktracker.model;

public enum TaskStatus {
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
