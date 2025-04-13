package com.example.eatelo;

import android.content.Intent;
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

public class RankingPageActivity extends AppCompatActivity {

    private ListView listView;
    private EditText searchBar;
    private Button submitButton;
    private RecyclerView rankedRecyclerView;
    private RankedRestaurantsAdapter rankedAdapter;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> rankedRestaurants = new ArrayList<>();
    private ArrayList<String> restaurantList = new ArrayList<>();
    private String name, phone, password, bio, profileImageUri;
    private ArrayList<String> preferences;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_page);

        // Retrieve user data from Intent
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        bio = intent.getStringExtra("bio");
        password = intent.getStringExtra("password");
        preferences = intent.getStringArrayListExtra("preferences");
        profileImageUri = getIntent().getStringExtra("profileImageUri");

        // Initialize views
        searchBar = findViewById(R.id.searchBar);
        listView = findViewById(R.id.listView);
        rankedRecyclerView = findViewById(R.id.rankedListView);
        submitButton = findViewById(R.id.submitButton);

        // Load restaurant data from the database
        loadRestaurantData();

        // Initialize RecyclerView for ranked restaurants
        rankedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rankedAdapter = new RankedRestaurantsAdapter(this, rankedRestaurants, viewHolder -> {
            itemTouchHelper.startDrag(viewHolder);
        });



        rankedRecyclerView.setAdapter(rankedAdapter);

        // Setup ItemTouchHelper for drag and drop
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

        // Handle item clicks for ranking
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRestaurant = adapter.getItem(position); // Use filtered list
            if (!rankedRestaurants.contains(selectedRestaurant)) {
                if (rankedRestaurants.size() < 10) {
                    rankedRestaurants.add(selectedRestaurant);
                    rankedAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "You can only rank up to 10 restaurants!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Submit button to finalize ranking & store data
        submitButton.setOnClickListener(v -> {
            if (rankedRestaurants.size() < 10) {
                Toast.makeText(this, "Please rank at least 10 restaurants!", Toast.LENGTH_SHORT).show();
            } else {
                saveUserToDatabase();
            }
        });

        // Implement Search Functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);  // Filter list based on input
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Load restaurant data from the database
    private void loadRestaurantData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        restaurantList = dbHelper.getAllRestaurants();

        // Initialize adapter and set it to ListView
        adapter = new ArrayAdapter<>(this, R.layout.list_item_restaurant, restaurantList);
        listView.setAdapter(adapter);
    }

    // Save user info, preferences, and rankings to the database
    private void saveUserToDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long userId = dbHelper.addUser(phone, name, password, bio, profileImageUri);
        if (userId == -1) {
            Toast.makeText(this, "Error: User not added!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show();
            dbHelper.addPreferences(userId, preferences);
            dbHelper.addRankings(userId, rankedRestaurants);

            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("phone", phone);

            // Create a new task and clear everything
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Start the new task
            startActivity(intent);
            finish();
        }
    }
}
