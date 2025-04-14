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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdateRankingsActivity extends AppCompatActivity {

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
    private String phone;
    private ItemTouchHelper itemTouchHelper;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_page);

        // Initialize DatabaseHelper singleton
        dbHelper = DatabaseHelper.getInstance(this);

        // Get phone from intent
        phone = getIntent().getStringExtra("phone");
        if (phone == null) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupRecyclerView();
        loadRestaurantData();
        setupSearch();
    }

    private void initializeViews() {
        searchBar = findViewById(R.id.searchBar);
        listView = findViewById(R.id.listView);
        rankedRecyclerView = findViewById(R.id.rankedListView);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> validateAndSubmitRankings());
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

    private void loadRestaurantData() {
        restaurantList = dbHelper.getAllRestaurants();
        adapter = new ArrayAdapter<>(this, R.layout.list_item_restaurant, restaurantList);
        listView.setAdapter(adapter);
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

    private void validateAndSubmitRankings() {
        if (rankedRestaurants.size() < 10) {
            showToast("Please rank at least 10 restaurants!");
            return;
        }
        updateRankingsInDatabase();
    }

    private void updateRankingsInDatabase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            long userId = dbHelper.getUserId(db, phone);
            if (userId == -1) {
                showToast("User not found!");
                return;
            }

            // 1. Get previous rankings
            String oldRankingsJson = dbHelper.getRankings(db, userId);
            List<String> newRankings = rankedRestaurants;

            // 2. Compare with new rankings
            if (!haveRankingsChanged(oldRankingsJson, newRankings)) {
                showToast("Rankings unchanged - no updates needed");
                db.setTransactionSuccessful();
                finish();
                return;
            }

            // 3. Only update if rankings changed
            dbHelper.undoPreviousRanking(db, userId);
            dbHelper.applyNewRanking(db, userId, rankedRestaurants);
            dbHelper.updateRankings(db, userId, rankedRestaurants);

            db.setTransactionSuccessful();
            showToast("Rankings updated successfully!");
            finish();
        } finally {
            db.endTransaction();
        }
    }

    // Helper method to compare rankings
    private boolean haveRankingsChanged(String oldRankingsJson, List<String> newRankings) {
        if (oldRankingsJson == null) return true;

        try {
            JSONObject oldJson = new JSONObject(oldRankingsJson);
            if (oldJson.length() != newRankings.size()) return true;

            for (int i = 0; i < newRankings.size(); i++) {
                String restaurant = newRankings.get(i);
                if (!oldJson.has(restaurant) || oldJson.getInt(restaurant) != (i+1)) {
                    return true;
                }
            }
            return false;
        } catch (JSONException e) {
            return true;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}