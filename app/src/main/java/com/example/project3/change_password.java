package com.example.project3;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class change_password extends BottomSheetDialogFragment {
    public static final String TAG = "changePassword";
    // UI elements
    private EditText currentPassword, newPassword, confirmNewPassword;
    private Button btnSave;
    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    // Context field
    private Context context;

    public static change_password newInstance() {// Factory method to create a new instance of the fragment
        return new change_password();
    }

    @Override
    public void onAttach(@NonNull Context context) {// Attach context to the fragment
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    // Inflate the layout for the fragment
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {// Set up the view and initialize components
        super.onViewCreated(view, savedInstanceState);
        // Initialize UI elements
        currentPassword = view.findViewById(R.id.evCurrentPassword);
        newPassword = view.findViewById(R.id.evNewPassword);
        confirmNewPassword = view.findViewById(R.id.evCheckPassword);
        btnSave = view.findViewById(R.id.btnSave);
        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnSave.setOnClickListener(new View.OnClickListener() {// Set the save button click listener
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }

    private void changePassword() {// Method to change the password
        // Get the input values
        String currentPass = currentPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmNewPass = confirmNewPassword.getText().toString().trim();
        // Check if any field is empty
        if (TextUtils.isEmpty(currentPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmNewPass)) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmNewPass)) {// Check if new passwords match
            Toast.makeText(context, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mUser != null && mUser.getEmail() != null) {// Ensure the user is logged in and has an email
            // Create an authentication credential with the current password
            AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), currentPass);

            // Re-authenticate the user
            mUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Update the password
                    mUser.updatePassword(newPass).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            dismiss(); // Close the bottom sheet
                        } else {
                            Log.e("ChangePassword", "Error updating password", task1.getException());
                            Toast.makeText(context, "Error updating password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e("ChangePassword", "Error re-authenticating", task.getException());
                    Toast.makeText(context, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
