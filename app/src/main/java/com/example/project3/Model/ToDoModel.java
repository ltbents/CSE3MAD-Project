package com.example.project3.Model;

public class ToDoModel extends TaskId {
    private String task, due;
    private int status;

    public CharSequence getTask() {
        return task;
    }

    public CharSequence getDue() {
        return due;
    }

    public int getStatus() {
        return status;
    }
}
