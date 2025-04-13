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
        if (position == 0) {
            RecommendationsFragment recommendationsFragment = new RecommendationsFragment();
            Bundle args = new Bundle();
            args.putString("phone", userPhone);
            recommendationsFragment.setArguments(args);
            return recommendationsFragment;
        }
        else if (position == 1) {
            TopRestaurantsFragment topRestaurantsFragment = new TopRestaurantsFragment();
            Bundle args = new Bundle();
            args.putString("phone", userPhone);
            topRestaurantsFragment.setArguments(args);
            return topRestaurantsFragment;
        }
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean containsItem(long itemId) {
        return false; // This forces all fragments to be recreated
    }

}

