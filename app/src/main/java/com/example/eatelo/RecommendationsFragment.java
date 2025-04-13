package com.example.eatelo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecommendationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RestaurantAdapter2 adapter;
    private List<Restaurant> restaurantList;
    private TextView tvName;
    private String phone;

    public RecommendationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendations, container, false);

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
        adapter = new RestaurantAdapter2(restaurantList);
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
        adapter = new RestaurantAdapter2(restaurantList);
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

    private void fetchTopRestaurants() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<Restaurant> topRestaurants = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT restaurant_name, elo, address FROM restaurants", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                int elo = cursor.getInt(1);
                String address = cursor.getString(2);
                topRestaurants.add(new Restaurant(name, elo, address));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        topRestaurants.sort((r1, r2) -> Integer.compare(r2.getElo(), r1.getElo()));
        restaurantList = topRestaurants;
    }
}
