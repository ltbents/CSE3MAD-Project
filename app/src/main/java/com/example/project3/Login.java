package com.example.project3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity {
    // UI elements
    TextInputEditText editTextEmail, editTextPassword;
    Button btnLog;
    // Firebase Authentication instance
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    FloatingActionButton btnBack;
    Handler handler = new Handler();
    Runnable hidePasswordRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge experience
        setContentView(R.layout.activity_login);// Set the content view to the login activity layout
        mAuth = FirebaseAuth.getInstance();// Initialize Firebase Auth instance
        // Initialize UI elements
        btnBack = findViewById(R.id.btnBack);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        btnLog = findViewById(R.id.btnLogLogin);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);

        btnBack.setOnClickListener(new View.OnClickListener() {// Set the back button click listener
            @Override
            public void onClick(View view) {
                // Navigate to welcome page
                Intent intent = new Intent(getApplicationContext(), welcomePage.class);
                startActivity(intent);
                finish();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {// Set the register text view click listener
            @Override
            public void onClick(View view) {// Navigate to register page
                Intent intent =  new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        btnLog.setOnClickListener(new View.OnClickListener() {// Set the login button click listener
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);// Show progress bar
                // Get the email and password from the input fields
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    // Validate the input fields
                    Toast.makeText(Login.this,"Enter email and password",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                else if (TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this,"Enter email",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this,"Enter password",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                else {
                    mAuth.signInWithEmailAndPassword(email, password)// Sign in with email and password
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE); // Hide progress bar
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser(); // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);// Navigate to main activity
                                        startActivity(intent);
                                        finish();
                                    } else { // If sign in fails, display a message to the user
                                        Toast.makeText(Login.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                }
            }
        });
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(hidePasswordRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                hidePasswordRunnable = new Runnable() {
                    @Override
                    public void run() {
                        int cursorPosition = editTextPassword.getSelectionStart(); // Save cursor position
                        editTextPassword.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
                        editTextPassword.setSelection(cursorPosition); // Restore cursor position
                    }
                };
                handler.postDelayed(hidePasswordRunnable, 1000); // 1 second delay
            }
        });



    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(hidePasswordRunnable);
    }
}