package com.example.eatelo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class PreferencePageActivity extends AppCompatActivity {

    private final ArrayList<String> selectedPreferences = new ArrayList<>();
    private String name, phone, password; // Store user details

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        Button nextButton = findViewById(R.id.next);


        // Receive user details from SignupActivity
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        password = intent.getStringExtra("password");

        // Initialize buttons
        Button ambienceButton = findViewById(R.id.ambience);
        Button foodQualityButton = findViewById(R.id.food_quality);
        Button priceButton = findViewById(R.id.price);
        Button locationButton = findViewById(R.id.location);
        Button serviceButton = findViewById(R.id.service);
        Button hygieneButton = findViewById(R.id.hygiene);
        Button speedOfServiceButton = findViewById(R.id.speed_of_service);
        Button portionSizeButton = findViewById(R.id.portion_size);
        Button affordabilityButton = findViewById(R.id.affordability);
        Button multiCuisineButton = findViewById(R.id.multi_cuisine);
        Button outdoorSeatingButton = findViewById(R.id.outdoor_seating);
        Button beverageSelectionButton = findViewById(R.id.beverage_selection);

        // Set up click listeners for all preference buttons
        setupPreferenceButton(ambienceButton, "Ambience");
        setupPreferenceButton(foodQualityButton, "Food Quality");
        setupPreferenceButton(priceButton, "Price");
        setupPreferenceButton(locationButton, "Location");
        setupPreferenceButton(serviceButton, "Service");
        setupPreferenceButton(hygieneButton, "Hygiene");
        setupPreferenceButton(speedOfServiceButton, "Speed of Service");
        setupPreferenceButton(portionSizeButton, "Portion Size");
        setupPreferenceButton(affordabilityButton, "Affordability");
        setupPreferenceButton(multiCuisineButton, "Multi-Cuisine");
        setupPreferenceButton(outdoorSeatingButton, "Outdoor Seating");
        setupPreferenceButton(beverageSelectionButton, "Beverage Selection");

        // Next button click listener
        nextButton.setOnClickListener(v -> {
            if (selectedPreferences.isEmpty()) {
                Toast.makeText(this, "Please select at least one preference", Toast.LENGTH_SHORT).show();
            } else {
                // Move to ProfileActivity with user data & preferences
                Intent rankingIntent = new Intent(PreferencePageActivity.this, ProfileActivity.class);
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
                button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.beige))); // Default color
                button.setTextColor(Color.BLACK); // Change text color if needed
            } else {
                selectedPreferences.add(preference);
                button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.accent_red))); // Selected color (Red example)
                button.setTextColor(Color.WHITE); // Change text color for contrast
            }
        });
    }



}
