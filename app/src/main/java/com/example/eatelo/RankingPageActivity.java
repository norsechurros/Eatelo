package com.example.eatelo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class RankingPageActivity extends AppCompatActivity {

    private ListView listView;
    private TextView rankedTextView;
    private Button submitButton;
    private ArrayList<String> rankedRestaurants = new ArrayList<>();
    private ArrayList<String> restaurantList = new ArrayList<>();
    private String name, phone, password;
    private ArrayList<String> preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_page);

        // Retrieve user data from Intent
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        password = intent.getStringExtra("password");
        preferences = intent.getStringArrayListExtra("preferences");

        // Initialize views
        listView = findViewById(R.id.listView);
        rankedTextView = findViewById(R.id.textView);
        submitButton = findViewById(R.id.submitButton);

        // Load restaurant data (this should be fetched from the database)
        loadRestaurantData();

        // Adapter for ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restaurantList);
        listView.setAdapter(adapter);

        // Handle item clicks for ranking
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRestaurant = restaurantList.get(position);
            if (!rankedRestaurants.contains(selectedRestaurant)) {
                if (rankedRestaurants.size() < 10) {
                    rankedRestaurants.add(selectedRestaurant);
                    updateRankedText();
                } else {
                    Toast.makeText(this, "You can only rank up to 10 restaurants!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Submit button to finalize ranking & store data
        submitButton.setOnClickListener(v -> {
            if (rankedRestaurants.size() < 3) {
                Toast.makeText(this, "Please rank at least 3 restaurants!", Toast.LENGTH_SHORT).show();
            } else {
                saveUserToDatabase();
            }
        });
    }

    // Load restaurant data (this should fetch from a database in the future)
    private void loadRestaurantData() {
        restaurantList.add("Restaurant A");
        restaurantList.add("Restaurant B");
        restaurantList.add("Restaurant C");
        restaurantList.add("Restaurant D");
        restaurantList.add("Restaurant E");
        restaurantList.add("Restaurant F");
    }

    // Update TextView with ranked restaurants
    private void updateRankedText() {
        StringBuilder rankedText = new StringBuilder("Your Ranking:\n");

        for (int i = 0; i < rankedRestaurants.size(); i++) {
            rankedText.append(i + 1).append(". ").append(rankedRestaurants.get(i)).append("\n");
        }

        rankedTextView.setText(rankedText.toString());
    }

    // Save user info, preferences, and rankings to the database
    private void saveUserToDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long userId = dbHelper.addUser(phone, name, password);

        if (userId == -1) {
            Toast.makeText(this, "Error: User not added!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show();

            dbHelper.addPreferences(userId, preferences);  // Updated to store CSV
            dbHelper.addRankings(userId, rankedRestaurants);  // Updated to store JSON
        }
    }
}

