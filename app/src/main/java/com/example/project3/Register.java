package com.example.project3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Register extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;// Text input fields for email and password
    Button btnReg;// Register button
    FirebaseAuth mAuth;// Firebase authentication instance
    ProgressBar progressBar;// Progress bar to indicate registration progress
    TextView textView;// Text view to navigate to login page
    FloatingActionButton btnBack; // Floating action button to navigate back to welcome page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);// Enable edge-to-edge display
        setContentView(R.layout.activity_register);// Set layout for register activity
        mAuth = FirebaseAuth.getInstance();// Initialize Firebase authentication instance
        editTextEmail = findViewById(R.id.email); // Get reference to email text input field
        editTextPassword = findViewById(R.id.password);// Get reference to password text input field
        btnReg = findViewById(R.id.btnRegRegister);// Get reference to register button
        progressBar = findViewById(R.id.progressBar);// Get reference to progress bar
        textView = findViewById(R.id.loginNow);// Get reference to text view to navigate to login page
        btnBack = findViewById(R.id.btnBack);// Get reference to back button to navigate back to welcome page

        btnBack.setOnClickListener(new View.OnClickListener() {
            // Navigate back to welcome page
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( Register.this, welcomePage.class);
                startActivity(intent);
                finish();// Finish current activity
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            // Navigate to login page
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            // Register button click listener
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); // Show progress bar
                String email, password;
                email = String.valueOf(editTextEmail.getText());// Get email input
                password = String.valueOf(editTextPassword.getText());// Get password input

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    // Check if email or password is empty
                    Toast.makeText(Register.this,"Enter email and password",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);// Hide progress bar
                    return ;
                }
                else if (TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this,"Enter email",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this,"Enter password",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                else {// Create user with email and password
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {// Registration successful
                                        Toast.makeText(Register.this, "Account created",
                                                Toast.LENGTH_SHORT).show();

                                    } else {// Registration failed
                                        Toast.makeText(Register.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
