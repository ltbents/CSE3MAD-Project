package com.example.project3;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project3.Model.ToDoModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class addNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "addNewTask";
    private TextView setDueDate;
    private TextView setReminder;
    private EditText desTask;
    private Button btnSave;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userID;
    private Context context;
    private String dueDate = "";
    private String id = "";
    private String dueDateUpdate = "";
    private  String reminderTime = "";
    private  String reminderUpdate = "";

    public static addNewTask newInstance() {
        return new addNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDueDate = view.findViewById(R.id.tv_set_due);
        setReminder = view.findViewById(R.id.tv_reminder);
        desTask = view.findViewById(R.id.task_edittext);
        btnSave = view.findViewById(R.id.btnSave);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser != null){
            userID = mUser.getUid();
        }
        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            id = bundle.getString("id");
            dueDateUpdate = bundle.getString("due");
            reminderUpdate = bundle.getString("reminder");
            desTask.setText(task);
            setDueDate.setText(dueDateUpdate);
            setReminder.setText(reminderUpdate);
            if(task.length()>0){
                btnSave.setEnabled(false);
                btnSave.setBackgroundColor(Color.GRAY);
            }
        }

        desTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    btnSave.setEnabled(false);
                    btnSave.setBackgroundColor(Color.GRAY);
                } else {
                    btnSave.setEnabled(true);
                    btnSave.setBackgroundColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        boolean finalIsUpdate = isUpdate;
        setDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        selectedMonth = selectedMonth + 1;
                        dueDate = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                        setDueDate.setText(dueDate);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        setReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selected_hour, int selected_minute) {
                        reminderTime = selected_hour + ":" + selected_minute;
                        setReminder.setText(reminderTime);
                    }
                },hour, minute, true);
                timePickerDialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskDescription = desTask.getText().toString();

//                if(finalIsUpdate){
//                    firestore.collection("task").document(id).update("task", taskDescription , "due", dueDate);
//                    Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();
//                }

                    if (taskDescription.isEmpty() || dueDate.isEmpty() || reminderTime.isEmpty()) {
                        Toast.makeText(context, "Fill the blank", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {

                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("task", taskDescription);
                        taskMap.put("due", dueDate);
                        taskMap.put("status", 0);
                        taskMap.put("reminder",reminderTime);
                        taskMap.put("time", FieldValue.serverTimestamp());
                        if(userID != null){
                        firestore.collection("users").document(userID).collection("task").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Task Saved", Toast.LENGTH_SHORT).show();
                                    String taskId =task.getResult().getId();
                                    scheduleReminder(taskId,taskMap);
                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                dismiss();
            }
        });
    }
    private void scheduleReminder(String taskId, Map<String, Object> taskMap) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra("taskId", taskId);
        intent.putExtra("taskDescription", taskMap.get("task").toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String[] timeParts = reminderTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }
}
