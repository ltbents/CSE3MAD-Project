package com.example.project3;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class change_password extends BottomSheetDialogFragment {
    public static final String TAG = "changePassword";
    private TextInputEditText currentPassword, newPassword, confirmNewPassword;
    private Button btnSave;
    private CheckBox showCurrentPassword, showNewPassword, showConfirmNewPassword;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Context context;
    Handler handler = new Handler();
    Runnable hidePasswordRunnable;

    public static change_password newInstance() {
        return new change_password();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentPassword = view.findViewById(R.id.evCurrentPassword);
        newPassword = view.findViewById(R.id.evNewPassword);
        confirmNewPassword = view.findViewById(R.id.evCheckPassword);
        btnSave = view.findViewById(R.id.btnSave);
        showCurrentPassword = view.findViewById(R.id.showCurrentPassword);
        showNewPassword = view.findViewById(R.id.showNewPassword);
        showConfirmNewPassword = view.findViewById(R.id.showConfirmNewPassword);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        setupPasswordVisibilityToggle(currentPassword, showCurrentPassword);
        setupPasswordVisibilityToggle(newPassword, showNewPassword);
        setupPasswordVisibilityToggle(confirmNewPassword, showConfirmNewPassword);
    }

    private void setupPasswordVisibilityToggle(TextInputEditText passwordField, CheckBox checkBox) {
        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(hidePasswordRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                hidePasswordRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!checkBox.isChecked()) {
                            int cursorPosition = passwordField.getSelectionStart();
                            passwordField.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
                            passwordField.setSelection(cursorPosition);
                        }
                    }
                };
                handler.postDelayed(hidePasswordRunnable, 1000);
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int cursorPosition = passwordField.getSelectionStart();
                    passwordField.setTransformationMethod(null);
                    passwordField.setSelection(cursorPosition);
                } else {
                    int cursorPosition = passwordField.getSelectionStart();
                    passwordField.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
                    passwordField.setSelection(cursorPosition);
                }
            }
        });
    }



    private void changePassword() {
        String currentPass = currentPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmNewPass = confirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmNewPass)) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmNewPass)) {
            Toast.makeText(context, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mUser != null && mUser.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), currentPass);

            mUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mUser.updatePassword(newPass).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            dismiss();
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
