package com.example.eatelo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

    // UI Components
    private ListView listView;
    private EditText searchBar;
    private Button submitButton;
    private RecyclerView rankedRecyclerView;

    // Adapters
    private RankedRestaurantsAdapter rankedAdapter;
    private ArrayAdapter<String> adapter;

    // Data
    private ArrayList<String> rankedRestaurants = new ArrayList<>();
    private ArrayList<String> restaurantList = new ArrayList<>();
    private String name, phone, password, bio, profileImageUri;
    private ArrayList<String> preferences;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_page);

        // Initialize UI and data
        initViews();
        loadIntentData();
        setupRecyclerView();
        setupListView();
        setupSearch();
    }

    private void initViews() {
        searchBar = findViewById(R.id.searchBar);
        listView = findViewById(R.id.listView);
        rankedRecyclerView = findViewById(R.id.rankedListView);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> validateAndSubmit());
    }

    private void loadIntentData() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        bio = intent.getStringExtra("bio");
        password = intent.getStringExtra("password");
        preferences = intent.getStringArrayListExtra("preferences");
        profileImageUri = intent.getStringExtra("profileImageUri");
    }

    private void setupRecyclerView() {
        rankedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankedAdapter = new RankedRestaurantsAdapter(this, rankedRestaurants,
                viewHolder -> itemTouchHelper.startDrag(viewHolder));

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                rankedAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {}
        };

        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rankedRecyclerView);
        rankedRecyclerView.setAdapter(rankedAdapter);
    }

    private void setupListView() {
        loadRestaurantData();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRestaurant = adapter.getItem(position);
            if (!rankedRestaurants.contains(selectedRestaurant)) {
                if (rankedRestaurants.size() < 10) {
                    rankedRestaurants.add(selectedRestaurant);
                    rankedAdapter.notifyDataSetChanged();
                } else {
                    showToast("You can only rank up to 10 restaurants!");
                }
            }
        });
    }

    private void setupSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadRestaurantData() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        restaurantList = dbHelper.getAllRestaurants();
        adapter = new ArrayAdapter<>(this, R.layout.list_item_restaurant, restaurantList);
        listView.setAdapter(adapter);
    }

    private void validateAndSubmit() {
        if (rankedRestaurants.size() < 10) {
            showToast("Please rank at least 10 restaurants!");
            return;
        }
        saveUserToDatabase();
    }

    private void saveUserToDatabase() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            long userId = dbHelper.addUser(db, phone, name, password, bio, profileImageUri);
            if (userId == -1) {
                showToast("Error: User not added!");
                return;
            }

            // Save all user data in transaction
            dbHelper.addPreferences(db, userId, preferences);
            dbHelper.addRankings(db, userId, rankedRestaurants);
            dbHelper.applyNewRanking(db, userId, rankedRestaurants);

            db.setTransactionSuccessful();
            showToast("User added successfully!");
            launchDashboard();

        } catch (Exception e) {
            Log.e("Database", "Error saving user", e);
            showToast("Error saving data!");
        } finally {
            if (db != null) {
                try {
                    if (db.inTransaction()) {
                        db.endTransaction();
                    }
                } catch (Exception e) {
                    Log.e("Database", "Error ending transaction", e);
                }
            }
        }
    }

    private void launchDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class)
                .putExtra("phone", phone)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}