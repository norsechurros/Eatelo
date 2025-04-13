package com.example.eatelo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class UpdatePreferencesActivity extends AppCompatActivity {

    private final ArrayList<String> selectedPreferences = new ArrayList<>();
    private String phone; // Phone number from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_preferences);

        // Retrieve phone number from intent
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");

        // Initialize buttons
        Button nextButton = findViewById(R.id.next);
        Button ambienceButton = findViewById(R.id.ambience);
        Button foodQualityButton = findViewById(R.id.food_quality);
        Button serviceButton = findViewById(R.id.service);
        Button hygieneButton = findViewById(R.id.hygiene);
        Button valueforMoneyButton = findViewById(R.id.value_for_money);
        Button portionSizeButton = findViewById(R.id.portion_size);

        // Set up click listeners for all preference buttons
        setupPreferenceButton(ambienceButton, "Ambience");
        setupPreferenceButton(foodQualityButton, "Food Quality");
        setupPreferenceButton(serviceButton, "Service");
        setupPreferenceButton(hygieneButton, "Hygiene");
        setupPreferenceButton(valueforMoneyButton, "Value for Money");
        setupPreferenceButton(portionSizeButton, "Portion Size");

        // Update preferences in database
        nextButton.setOnClickListener(v -> {
            if (selectedPreferences.isEmpty()) {
                Toast.makeText(this, "Please select at least one preference", Toast.LENGTH_SHORT).show();
            } else {
                String updatedPrefs = String.join(",", selectedPreferences);

                DatabaseHelper dbHelper = new DatabaseHelper(this);
                boolean success = dbHelper.updateUserPreferences(phone, updatedPrefs);

                if (success) {
                    Toast.makeText(this, "Preferences updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Return to previous screen
                } else {
                    Toast.makeText(this, "Failed to update preferences", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setupPreferenceButton(Button button, String preference) {
        button.setOnClickListener(v -> {
            if (selectedPreferences.contains(preference)) {
                selectedPreferences.remove(preference);
                button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.beige)));
                button.setTextColor(getColor(R.color.black));
            } else {
                selectedPreferences.add(preference);
                button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.accent_red)));
                button.setTextColor(getColor(R.color.white));
            }
        });
    }
}
