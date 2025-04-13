package com.example.eatelo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class UpdateRankingsActivity extends AppCompatActivity {

    private ListView listView;
    private EditText searchBar;
    private Button submitButton;
    private RecyclerView rankedRecyclerView;
    private RankedRestaurantsAdapter rankedAdapter;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> rankedRestaurants = new ArrayList<>();
    private ArrayList<String> restaurantList = new ArrayList<>();
    private String phone;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_page); // Reuse same layout

        // Get phone number from Intent
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");

        // Initialize views
        searchBar = findViewById(R.id.searchBar);
        listView = findViewById(R.id.listView);
        rankedRecyclerView = findViewById(R.id.rankedListView);
        submitButton = findViewById(R.id.submitButton);

        // Load restaurant data from the database
        loadRestaurantData();

        // Setup RecyclerView
        rankedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankedAdapter = new RankedRestaurantsAdapter(this, rankedRestaurants, viewHolder -> {
            itemTouchHelper.startDrag(viewHolder);
        });
        rankedRecyclerView.setAdapter(rankedAdapter);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                rankedAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {}
        };
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rankedRecyclerView);

        // Handle restaurant selection
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRestaurant = adapter.getItem(position);
            if (!rankedRestaurants.contains(selectedRestaurant)) {
                if (rankedRestaurants.size() < 10) {
                    rankedRestaurants.add(selectedRestaurant);
                    rankedAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "You can only rank up to 10 restaurants!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Save updated rankings
        submitButton.setOnClickListener(v -> {
            if (rankedRestaurants.size() < 3) {
                Toast.makeText(this, "Please rank at least 3 restaurants!", Toast.LENGTH_SHORT).show();
            } else {
                updateRankingsInDatabase();
            }
        });
    }

    private void loadRestaurantData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        restaurantList = dbHelper.getAllRestaurants();
        adapter = new ArrayAdapter<>(this, R.layout.list_item_restaurant, restaurantList);
        listView.setAdapter(adapter);
    }

    private void updateRankingsInDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // Open database

        // Get user ID first, pass db to getUserId
        long userId = dbHelper.getUserId(db, phone);  // Pass db to getUserId
        if (userId != -1) {
            dbHelper.updateRankings(db, userId, rankedRestaurants); // Pass db to updateRankings
            Toast.makeText(this, "Rankings updated successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Optionally finish the activity after saving
        } else {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
        }

        db.close(); // Close the database after operation
    }

}
