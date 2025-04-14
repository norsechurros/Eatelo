package com.example.eatelo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class UpdatePreferencesActivity extends AppCompatActivity {

    private final ArrayList<String> selectedPreferences = new ArrayList<>();
    private String phone;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_preferences);

        // Initialize DatabaseHelper singleton
        dbHelper = DatabaseHelper.getInstance(this);

        // Get phone from intent
        phone = getIntent().getStringExtra("phone");
        if (phone == null) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
    }

    private void initViews() {
        Button nextButton = findViewById(R.id.next);
        Button[] preferenceButtons = {
                findViewById(R.id.ambience),
                findViewById(R.id.food_quality),
                findViewById(R.id.service),
                findViewById(R.id.hygiene),
                findViewById(R.id.value_for_money),
                findViewById(R.id.portion_size)
        };

        String[] preferences = {
                "Ambience",
                "Food Quality",
                "Service",
                "Hygiene",
                "Value for Money",
                "Portion Size"
        };

        // Set up all preference buttons
        for (int i = 0; i < preferenceButtons.length; i++) {
            setupPreferenceButton(preferenceButtons[i], preferences[i]);
        }

        nextButton.setOnClickListener(v -> updatePreferences());
    }

    private void setupPreferenceButton(Button button, String preference) {
        button.setOnClickListener(v -> togglePreference(button, preference));
    }

    private void togglePreference(Button button, String preference) {
        if (selectedPreferences.contains(preference)) {
            selectedPreferences.remove(preference);
            button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.beige)));
            button.setTextColor(getColor(R.color.black));
        } else {
            selectedPreferences.add(preference);
            button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.accent_red)));
            button.setTextColor(getColor(R.color.white));
        }
    }

    private void updatePreferences() {
        if (selectedPreferences.isEmpty()) {
            Toast.makeText(this, "Please select at least one preference", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String updatedPrefs = TextUtils.join(",", selectedPreferences);
            boolean success = dbHelper.updateUserPreferences(db, phone, updatedPrefs);

            if (success) {
                Toast.makeText(this, "Preferences updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update preferences", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}