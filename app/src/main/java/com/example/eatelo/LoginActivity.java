package com.example.eatelo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText phoneInput, passwordInput;
    private Button loginButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize database helper using singleton pattern
        dbHelper = DatabaseHelper.getInstance(this);

        // Initialize UI elements
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        // Handle login button click
        loginButton.setOnClickListener(v -> loginUser());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // No need to close dbHelper here - singleton manages its own lifecycle
    }

    private void loginUser() {
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter phone and password!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check user credentials in database
        if (dbHelper.validateUser(phone, password)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("phone", phone);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
        }
    }
}