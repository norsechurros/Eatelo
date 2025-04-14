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
import java.util.Collections;
import java.util.List;

public class RecommendationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RestaurantAdapter2 adapter;
    private List<Restaurant> restaurantList;
    private TextView tvName;
    private String phone;
    private DatabaseHelper dbHelper;

    public RecommendationsFragment() {
        // Required empty public constructor
    }

    public static RecommendationsFragment newInstance(String phone) {
        RecommendationsFragment fragment = new RecommendationsFragment();
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
        View view = inflater.inflate(R.layout.fragment_recommendations, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // No need to close dbHelper - singleton manages its own lifecycle
    }

    private void initViews(View view) {
        tvName = view.findViewById(R.id.tvname);
        recyclerView = view.findViewById(R.id.recyclerViewTopRestaurants);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        phone = getArguments() != null ? getArguments().getString("phone") : null;
        refreshData();
    }

    private void refreshData() {
        if (phone != null) {
            String userName = getUserNameFromDatabase(phone);
            tvName.setText(userName != null ? userName : "User");
        }
        fetchTopRestaurants();
        adapter = new RestaurantAdapter2(restaurantList);
        recyclerView.setAdapter(adapter);
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

    private void fetchTopRestaurants() {
        restaurantList = new ArrayList<>();
        try (Cursor cursor = dbHelper.getReadableDatabase().query(
                DatabaseHelper.TABLE_RESTAURANTS,
                new String[]{
                        DatabaseHelper.COLUMN_RESTAURANT_NAME,
                        DatabaseHelper.COLUMN_RESTAURANT_ELO,
                        DatabaseHelper.COLUMN_ADDRESS
                },
                null, null, null, null,
                DatabaseHelper.COLUMN_RESTAURANT_ELO + " DESC")) {

            while (cursor.moveToNext()) {
                restaurantList.add(new Restaurant(
                        cursor.getString(0),
                        cursor.getInt(1),
                        cursor.getString(2)
                ));
            }
        }
    }
}