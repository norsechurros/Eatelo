package com.example.eatelo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TopRestaurantsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> restaurantList;
    private TextView tvName;
    private String phone;

    public TopRestaurantsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_restaurants, container, false);

        // Get phone from arguments safely
        phone = getArguments() != null ? getArguments().getString("phone") : null;

        tvName = view.findViewById(R.id.tvname);
        recyclerView = view.findViewById(R.id.recyclerViewTopRestaurants);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set user name from DB
        if (phone != null) {
            String userName = getUserNameFromDatabase(phone);
            tvName.setText(userName);
        }

        fetchTopRestaurants();
        adapter = new RestaurantAdapter(restaurantList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        phone = getArguments() != null ? getArguments().getString("phone") : null;

        if (phone != null) {
            String userName = getUserNameFromDatabase(phone);
            tvName.setText(userName);
        }

        fetchTopRestaurants();
        adapter = new RestaurantAdapter(restaurantList);
        recyclerView.setAdapter(adapter);
    }

    private String getUserNameFromDatabase(String phone) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String name = "User";
        Cursor cursor = db.rawQuery("SELECT name FROM users WHERE phone = ?", new String[]{phone});
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return name;
    }

    private List<String> parseRankingsJson(String rankingsJson) {
        List<String> rankedRestaurantNames = new ArrayList<>();

        try {
            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(rankingsJson);
            Iterator<String> keys = jsonObject.keys();

            // Extract restaurant names from the JSON object
            while (keys.hasNext()) {
                String restaurantName = keys.next();
                rankedRestaurantNames.add(restaurantName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rankedRestaurantNames;
    }

    private void fetchTopRestaurants() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<Restaurant> topRestaurants = new ArrayList<>();

        // Get phone number from arguments
        String phone = getArguments() != null ? getArguments().getString("phone") : null;

        if (phone != null) {
            // Get the user ID based on the phone number
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            long userId = dbHelper.getUserId(db, phone); // Use dbHelper to fetch user ID

            if (userId != -1) {
                // Get the rankings for the user from the database
                String rankingsJson = dbHelper.getRankings(db, userId); // Fetch the rankings JSON from the database
                if (rankingsJson != null) {
                    // Parse the rankings JSON to get the list of ranked restaurant names
                    List<String> rankedRestaurantNames = parseRankingsJson(rankingsJson);

                    // Fetch restaurant details in the order of rankings (already ordered)
                    for (String restaurantName : rankedRestaurantNames) {
                        Restaurant restaurant = dbHelper.getRestaurantByName(db, restaurantName); // Get restaurant details by name
                        if (restaurant != null) {
                            topRestaurants.add(restaurant);
                        }
                    }
                }
            } else {
                Toast.makeText(getContext(), "User not found!", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }

        restaurantList = topRestaurants;
    }


}
