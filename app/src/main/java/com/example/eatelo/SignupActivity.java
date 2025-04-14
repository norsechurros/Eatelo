package com.example.eatelo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    private EditText nameInput, phoneInput, passwordInput, confirmPasswordInput;

    // Validation constants
    private static final int MAX_NAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 15;
    private static final int PHONE_NUMBER_LENGTH = 10; // 10-digit phone numbers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Initialize UI elements
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.passwordconfirmInput);
        Button submitButton = findViewById(R.id.loginButton);

        submitButton.setOnClickListener(v -> attemptSignup());
    }

    private void attemptSignup() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (validateInputs(name, phone, password, confirmPassword)) {
            proceedToPreferences(name, phone, password);
        }
    }

    private boolean validateInputs(String name, String phone, String password, String confirmPassword) {
        // Check empty fields
        if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showToast("Please fill in all fields");
            return false;
        }

        // Name validation
        if (name.length() > MAX_NAME_LENGTH) {
            showToast("Name too long (max " + MAX_NAME_LENGTH + " chars)");
            return false;
        }

        // Phone validation
        if (phone.length() != PHONE_NUMBER_LENGTH || !phone.matches("\\d+")) {
            showToast("Please enter a valid " + PHONE_NUMBER_LENGTH + "-digit phone number");
            return false;
        }

        // Password validation
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            showToast("Password must be " + MIN_PASSWORD_LENGTH + "-" + MAX_PASSWORD_LENGTH + " characters");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords don't match");
            return false;
        }

        return true;
    }

    private void proceedToPreferences(String name, String phone, String password) {
        Intent intent = new Intent(this, PreferencePageActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}