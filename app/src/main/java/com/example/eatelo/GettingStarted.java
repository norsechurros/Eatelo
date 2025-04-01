package com.example.eatelo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GettingStarted extends AppCompatActivity {

    // Declare buttons
    private Button signInButton;
    private Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getting_started);

        // Initialize buttons
        signInButton = findViewById(R.id.signInButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        // Set click listener for Sign In button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go to login activity
                Intent intent = new Intent(GettingStarted.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for Create Account button
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go to signup activity
                Intent intent = new Intent(GettingStarted.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
