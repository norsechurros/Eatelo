package com.example.eatelo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    private EditText nameInput, phoneInput, passwordInput, confirmPasswordInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Initialize UI elements
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.passwordconfirmInput);
        submitButton = findViewById(R.id.loginButton);

        // Submit button click listener
        submitButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // Validate input fields
            if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed to Preference Page with user details
                Intent intent = new Intent(SignupActivity.this, PreferencePageActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });
    }
}
