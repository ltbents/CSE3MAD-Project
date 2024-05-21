package com.example.project3.Adapter;
// Import necessary Android and Firebase libraries
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project3.MainActivity;
import com.example.project3.Model.ToDoModel;
import com.example.project3.R;
import com.example.project3.addNewTask;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {
    // Adapter class for managing a list of ToDoModel items in a RecyclerView
    private List<ToDoModel> todoList;  // List of ToDoModel items
    private MainActivity activity;    // Reference to the MainActivity
    private FirebaseFirestore firestore; // Firestore instance for database operations

    public ToDoAdapter(MainActivity mainActivity, List<ToDoModel> todoList) {
        // Constructor for initializing the adapter with activity and todoList
        this.activity = mainActivity;
        this.todoList = todoList;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(activity).inflate(R.layout.each_task, parent, false);
        return new MyViewHolder(view);
    }
    public void deleteTask(int position){
        // Method to delete a task at a given position
        ToDoModel toDoModel = todoList.get(position);// Get the task model at the position
        firestore.collection("task").document(toDoModel.TaskId).delete();// Delete the task from Firestore
        todoList.remove(position);// Remove the task from the list
        notifyItemRemoved(position);// Notify RecyclerView about the item removal
    }
    public Context getContext(){
        // Get the context of the activity
        return activity;
    }

    public void editTask(int position){
        // Method to edit a task at a given position
        ToDoModel toDoModel = todoList.get(position);// Get the task model at the position
        Bundle bundle = new Bundle();
        bundle.putString("task" , toDoModel.getTask());// Put task details into the bundle
        bundle.putString("due" , toDoModel.getDue());
        bundle.putString("reminder",toDoModel.getReminder());
        bundle.putString("id" , toDoModel.TaskId);
        addNewTask AddnewTask = new addNewTask();// Create a new instance of addNewTask dialog
        AddnewTask.setArguments(bundle);// Set the arguments for the dialog
        AddnewTask.show(activity.getSupportFragmentManager(), AddnewTask.getTag());// Show the dialog
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Bind the data to the ViewHolder
        ToDoModel toDoModel = todoList.get(position);// Get the task model at the position
        holder.mCheckBox.setText(toDoModel.getTask());// Set the task description on the checkbox
        String dueDate = toDoModel.getDue();
        String reminder =toDoModel.getReminder();
        if (dueDate != null) {
            // Check if due date is set
            holder.mDueDateTv.setText(holder.itemView.getContext().getString(R.string.due_date_text, dueDate));
        }
        else {
            holder.mDueDateTv.setText(holder.itemView.getContext().getString(R.string.due_date_not_set));
        }
        if(reminder != null){// Check if reminder is set
            holder.mReminder.setText(holder.itemView.getContext().getString(R.string.set_time, reminder));
        }
        else {
            holder.mReminder.setText(holder.itemView.getContext().getString(R.string.not_set_time));
        }
        holder.mCheckBox.setChecked(toBoolean(toDoModel.getStatus()));// Set the checkbox state based on task status


        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // Set listener for checkbox state changes
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {// Update task status in Firestore
                    firestore.collection("task").document(toDoModel.TaskId).update("status", 1);
                } else {
                    firestore.collection("task").document(toDoModel.TaskId).update("status", 0);
                }
            }
        });
    }

    private boolean toBoolean(int status) {
        // Convert integer status to boolean
        return status != 0;
    }

    @Override
    public int getItemCount() {
        // Get the total number of items in the list
        return todoList.size();
    }

    public void addTask(ToDoModel task) {
        // Add a new task to the list
        todoList.add(task);// Add the task to the list
        notifyItemInserted(todoList.size() - 1);// Notify RecyclerView about the new item
    }

    public void setTasks(List<ToDoModel> tasks) {
        // Set the list of tasks and notify RecyclerView about the data change
        this.todoList = tasks;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder class for holding the views of each item
        TextView mDueDateTv;// TextView for due date
        TextView mReminder;// TextView for reminder
        CheckBox mCheckBox;// CheckBox for task status

        public MyViewHolder(@NonNull View itemView) {
            // Constructor for initializing the views
            super(itemView);
            mDueDateTv = itemView.findViewById(R.id.dueDate);// Initialize due date TextView
            mCheckBox = itemView.findViewById(R.id.checkBox);// Initialize checkbox
            mReminder = itemView.findViewById(R.id.reminder);// Initialize reminder TextView
        }
    }
}
