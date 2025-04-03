package com.example.eatelo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PreferencePageActivity extends AppCompatActivity {

    private final ArrayList<String> selectedPreferences = new ArrayList<>();
    private String name, phone, password; // Store user details

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        // Receive user details from SignupActivity
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        password = intent.getStringExtra("password");

        // Initialize buttons
        Button ambienceButton = findViewById(R.id.ambience);
        Button foodQualityButton = findViewById(R.id.foodqual);
        Button priceButton = findViewById(R.id.price);
        Button locationButton = findViewById(R.id.location);
        Button serviceButton = findViewById(R.id.service);
        Button hygieneButton = findViewById(R.id.hygiene);
        Button speedOfServiceButton = findViewById(R.id.speedOfService);
        Button portionSizeButton = findViewById(R.id.portionsize);
        Button availabilityButton = findViewById(R.id.avail);
        Button nextButton = findViewById(R.id.next);

        // Set up click listeners for all preference buttons
        setupPreferenceButton(ambienceButton, "Ambience");
        setupPreferenceButton(foodQualityButton, "Food Quality");
        setupPreferenceButton(priceButton, "Price");
        setupPreferenceButton(locationButton, "Location");
        setupPreferenceButton(serviceButton, "Service");
        setupPreferenceButton(hygieneButton, "Hygiene");
        setupPreferenceButton(speedOfServiceButton, "Speed of Service");
        setupPreferenceButton(portionSizeButton, "Portion Size");
        setupPreferenceButton(availabilityButton, "Availability");

        // Next button click listener
        nextButton.setOnClickListener(v -> {
            if (selectedPreferences.isEmpty()) {
                Toast.makeText(this, "Please select at least one preference", Toast.LENGTH_SHORT).show();
            } else {
                // Move to RankingPageActivity with user data & preferences
                Intent rankingIntent = new Intent(PreferencePageActivity.this, RankingPageActivity.class);
                rankingIntent.putExtra("name", name);
                rankingIntent.putExtra("phone", phone);
                rankingIntent.putExtra("password", password);
                rankingIntent.putStringArrayListExtra("preferences", selectedPreferences);
                startActivity(rankingIntent);
            }
        });
    }

    /**
     * Sets up a button to toggle selection and add/remove its text from the preferences array.
     */
    private void setupPreferenceButton(Button button, String preference) {
        button.setOnClickListener(v -> {
            if (selectedPreferences.contains(preference)) {
                selectedPreferences.remove(preference);
                button.setBackgroundColor(getResources().getColor(R.color.red)); // Original color
            } else {
                selectedPreferences.add(preference);
                button.setBackgroundColor(Color.GREEN); // Selected color
            }
        });
    }
}
