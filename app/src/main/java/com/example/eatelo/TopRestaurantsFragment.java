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
    private DatabaseHelper dbHelper;

    public TopRestaurantsFragment() {
        // Required empty public constructor
    }

    public static TopRestaurantsFragment newInstance(String phone) {
        TopRestaurantsFragment fragment = new TopRestaurantsFragment();
        Bundle args = new Bundle();
        args.putString("phone", phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_restaurants, container, false);
        initViews(view);
        loadData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews(View view) {
        tvName = view.findViewById(R.id.tvname);
        recyclerView = view.findViewById(R.id.recyclerViewTopRestaurants);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        phone = getArguments() != null ? getArguments().getString("phone") : null;
    }

    private void loadData() {
        if (phone != null) {
            String userName = getUserNameFromDatabase(phone);
            tvName.setText(userName != null ? userName : "User");
            fetchTopRestaurants();
            adapter = new RestaurantAdapter(restaurantList);
            recyclerView.setAdapter(adapter);
        }
    }

    private String getUserNameFromDatabase(String phone) {
        try (Cursor cursor = dbHelper.getReadableDatabase().query(
                DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_NAME},
                DatabaseHelper.COLUMN_PHONE + " = ?",
                new String[]{phone},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }
        return null;
    }

    private List<String> parseRankingsJson(String rankingsJson) {
        List<String> rankedRestaurantNames = new ArrayList<>();
        if (rankingsJson == null) return rankedRestaurantNames;

        try {
            JSONObject jsonObject = new JSONObject(rankingsJson);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                rankedRestaurantNames.add(keys.next());
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error parsing rankings", Toast.LENGTH_SHORT).show();
        }
        return rankedRestaurantNames;
    }

    private void fetchTopRestaurants() {
        restaurantList = new ArrayList<>();
        if (phone == null) return;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            long userId = dbHelper.getUserId(db, phone);
            if (userId == -1) {
                Toast.makeText(getContext(), "User not found!", Toast.LENGTH_SHORT).show();
                return;
            }

            String rankingsJson = dbHelper.getRankings(db, userId);
            List<String> rankedRestaurantNames = parseRankingsJson(rankingsJson);

            for (String restaurantName : rankedRestaurantNames) {
                Restaurant restaurant = dbHelper.getRestaurantByName(db, restaurantName);
                if (restaurant != null) {
                    restaurantList.add(restaurant);
                }
            }
        } finally {
            db.close();
        }
    }
}