package com.example.eatelo;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private ViewPager2 viewPager;
    private DashboardPagerAdapter pagerAdapter;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Get phone number from Intent
        userPhone = getIntent().getStringExtra("phone");

        bottomNav = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.viewPager);

        // Initialize pagerAdapter with user phone
        pagerAdapter = new DashboardPagerAdapter(this, userPhone);
        viewPager.setAdapter(pagerAdapter);

        bottomNav.setSelectedItemId(R.id.nav_top);

        bottomNav.setOnItemSelectedListener(item -> {
            int position = 0;
            if (item.getItemId() == R.id.nav_recommendations) {
                position = 0;
            } else if (item.getItemId() == R.id.nav_top) {
                position = 1;
            } else if (item.getItemId() == R.id.nav_profile) {
                position = 2;
            }

            viewPager.setCurrentItem(position, true);
            return true;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) bottomNav.setSelectedItemId(R.id.nav_recommendations);
                else if (position == 1) bottomNav.setSelectedItemId(R.id.nav_top);
                else if (position == 2) bottomNav.setSelectedItemId(R.id.nav_profile);
            }
        });
    }
}
