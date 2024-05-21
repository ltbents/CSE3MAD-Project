package com.example.project3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project3.Adapter.ToDoAdapter;
import com.example.project3.Model.ToDoModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements OnDialogCloseListener{
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FirebaseFirestore firestore;
    private ToDoAdapter adapter;
    private List<ToDoModel> mList;
    private Query query;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userID;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.reView);
        floatingActionButton = findViewById(R.id.floatBtnAct);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser != null){
            userID = mUser.getUid();
        }
        else{
            Log.e("MainActivity","User not authenticated");
            return;
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTask.newInstance().show(getSupportFragmentManager(), addNewTask.TAG);
            }
        });

        mList = new ArrayList<>();
        adapter = new ToDoAdapter(MainActivity.this, mList);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        showData();
        createNotificationChannel();
        recyclerView.setAdapter(adapter);
    }

    private void showData() {
        if (userID == null) {
            Log.e("MainActivity", "User ID is null");
            return;
        }

        query = firestore.collection("users").document(userID).collection("task").orderBy("time", Query.Direction.DESCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("MainActivity", "Listen failed: ", error);
                    return;
                }

                if (value != null) {
                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            String id = documentChange.getDocument().getId();
                            ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);
                            mList.add(toDoModel);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    listenerRegistration.remove();
                } else {
                    Log.e("MainActivity", "QuerySnapshot is null");
                }
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ReminderChannel";
            String description = "Channel for Task Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}
