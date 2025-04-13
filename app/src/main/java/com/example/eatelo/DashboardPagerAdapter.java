package com.example.eatelo;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    private final String userPhone;

    public DashboardPagerAdapter(@NonNull AppCompatActivity activity, String userPhone) {
        super(activity);
        this.userPhone = userPhone;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new RecommendationsFragment();
        else if (position == 1) return new TopRestaurantsFragment();
        else {
            // Pass phone number to ProfileFragment
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle args = new Bundle();
            args.putString("phone", userPhone);
            profileFragment.setArguments(args);
            return profileFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

