package com.example.project3.Model;

public class ToDoModel extends TaskId {
    // ToDoModel class extends TaskId to inherit task ID handling
    // Private fields for task details
    private String task;
    private String due;
    private int status;

    private String reminder;

    public String getReminder() {
        return reminder;
    } // Getter for reminder field

    public void setReminder(String reminder) {// Setter for reminder field
        this.reminder = reminder;
    }

    public String getTask() {
        return task;
    }// Getter for task field

    public void setTask(String task) {
        this.task = task;
    } // Setter for task field

    public String getDue() {
        return due;
    }// Getter for due field

    public void setDue(String due) {
        this.due = due;
    }    // Setter for due field

    public int getStatus() {
        return status;
    }
    // Getter for status field

    public void setStatus(int status) {
        this.status = status;
    }// Setter for status field
}
