package com.example.eatelo;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RankingPageActivity extends AppCompatActivity {

    private ListView listView;
    private TextView rankedTextView;
    private ArrayList<String> rankedRestaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_page);

        // Initialize views
        listView = findViewById(R.id.listView);
        rankedTextView = findViewById(R.id.textView);

        // Sample list of restaurants
        final ArrayList<String> restaurantList = new ArrayList<>();
        restaurantList.add("Restaurant A");
        restaurantList.add("Restaurant B");
        restaurantList.add("Restaurant C");
        restaurantList.add("Restaurant D");

        // Adapter for ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restaurantList);
        listView.setAdapter(adapter);

        // Handle item clicks to add to ranked list
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRestaurant = restaurantList.get(position);

            // Add selected restaurant to ranked list if not already added
            if (!rankedRestaurants.contains(selectedRestaurant)) {
                rankedRestaurants.add(selectedRestaurant);
                updateRankedText();
            }
        });
    }

    // Update the ranked TextView with selected restaurants in rank order
    private void updateRankedText() {
        StringBuilder rankedText = new StringBuilder();

        for (int i = 0; i < rankedRestaurants.size(); i++) {
            rankedText.append(i + 1).append(". ").append(rankedRestaurants.get(i)).append("\n");
        }

        rankedTextView.setText(rankedText.toString());
    }
}
