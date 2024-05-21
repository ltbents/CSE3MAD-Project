package com.example.project3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class welcomePage extends AppCompatActivity {
    Button btnWelLog;
    Button btnWelReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);
        // Initialize buttons
       btnWelLog = findViewById(R.id.btnwelLogin);
       btnWelLog.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               // Button click listener for login
               Intent intent = new Intent(getApplicationContext(),Login.class);
               startActivity(intent);// Start the login activity
               finish();
           }
       });
        // Initialize buttons
       btnWelReg = findViewById(R.id.btnWelCreateAccount);
       btnWelReg.setOnClickListener(new View.OnClickListener() {
           // Button click listener for creating an account
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), Register.class);
               startActivity(intent);// Start the register activity
               finish();
           }
       });
    }
}