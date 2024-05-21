package com.example.project3.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class TaskId {
    // This class is used to handle the task ID separately
    // TaskId field, excluded from Firestore serialization
    @Exclude
    public String TaskId;
    public <T extends TaskId> T withId(@NonNull final String id){
        // Generic method to set the task ID and return the instance with the set ID
        this.TaskId = id;// Assign the provided ID to the TaskId field
        return (T) this;// Return the current instance with the set ID
    }
}
