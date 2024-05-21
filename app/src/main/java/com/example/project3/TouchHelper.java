package com.example.project3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project3.Adapter.ToDoAdapter;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TouchHelper extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter;
    public TouchHelper(ToDoAdapter adapter) {// Constructor accepting the adapter
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Method called when an item is moved
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Method called when an item is swiped
        final int position = viewHolder.getAdapterPosition();
        if ( direction == ItemTouchHelper.RIGHT){ // Check the direction of swipe
            // Display confirmation dialog for task deletion
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            builder.setMessage("Are You Sure ?")
                    .setTitle("Delete Task")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.deleteTask(position);// Delete the task if confirmed
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.notifyItemChanged(position);// Notify adapter to redraw the item if canceled
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
        }else{
            adapter.editTask(position);// Edit the task if swiped left
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // Method to customize the appearance of swiped items
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeRightActionIcon(R.drawable.baseline_delete_24)// Set delete icon for right swipe
                .addSwipeRightBackgroundColor(Color.RED)// Set background color for right swipe
                .addSwipeLeftActionIcon(R.drawable.baseline_create_24)// Set edit icon for left swipe
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(adapter.getContext(), R.color.red))// Set background color for left swipe
                .create()
                .decorate();// Apply the decorator to the RecyclerView
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
