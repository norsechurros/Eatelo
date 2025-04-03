package com.example.eatelo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PreferencePageActivity extends AppCompatActivity {

    // Array to store selected preferences
    private final ArrayList<String> selectedPreferences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

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

        // Set up click listener for the Next button
        nextButton.setOnClickListener(v -> {
            if (selectedPreferences.isEmpty()) {
                Toast.makeText(this, "Please select at least one preference", Toast.LENGTH_SHORT).show();
            } else {
                // Display selected preferences (or pass them to another activity)
                Toast.makeText(this, "Selected Preferences: " + selectedPreferences, Toast.LENGTH_LONG).show();
            }

            // Navigate to RankingPageActivity
            Intent intent = new Intent(PreferencePageActivity.this, RankingPageActivity.class);
            startActivity(intent);


        });
    }

    /**
     * Sets up a button to toggle selection and add/remove its text from the preferences array.
     */
    private void setupPreferenceButton(Button button, String preference) {
        button.setOnClickListener(v -> {
            if (selectedPreferences.contains(preference)) {
                // Deselect the button and remove preference
                selectedPreferences.remove(preference);
                button.setBackgroundColor(getResources().getColor(R.color.red)); // Original color
            } else {
                // Select the button and add preference
                selectedPreferences.add(preference);
                button.setBackgroundColor(Color.GREEN); // Selected color
            }
        });
    }
}
