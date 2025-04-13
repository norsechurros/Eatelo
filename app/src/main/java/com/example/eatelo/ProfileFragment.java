package com.example.eatelo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private EditText nameEditText, bioEditText;
    private TextView preferencesTextView, rankingsTextView;
    private Button saveChangesButton, updatePreferencesButton, updateRankingsButton;

    private String userPhone;
    private DatabaseHelper dbHelper;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String phone) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("phone", phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userPhone = getArguments().getString("phone");
        }
        dbHelper = new DatabaseHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        nameEditText = view.findViewById(R.id.nameInput);
        bioEditText = view.findViewById(R.id.bioInput);
        saveChangesButton = view.findViewById(R.id.saveButton);
        updatePreferencesButton = view.findViewById(R.id.updatePreferencesButton);
        updateRankingsButton = view.findViewById(R.id.updateRankingButton);

        loadUserProfile();
        saveChangesButton.setOnClickListener(v -> {
            String updatedName = nameEditText.getText().toString().trim();
            String updatedBio = bioEditText.getText().toString().trim();
            dbHelper.updateUserProfile(userPhone, updatedName, updatedBio);
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

        });

        updatePreferencesButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UpdatePreferencesActivity.class);
            intent.putExtra("phone", userPhone);
            startActivity(intent);
        });

        updateRankingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UpdateRankingsActivity.class);
            intent.putExtra("phone", userPhone);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserProfile() {
        if (userPhone == null) return;

        Bitmap userImage = dbHelper.getUserImage(userPhone, requireContext());
        profileImage.setImageBitmap(userImage);

        String name = dbHelper.getUserName(userPhone);
        String bio = dbHelper.getUserBio(userPhone);
        nameEditText.setText(name);
        bioEditText.setText(bio);

    }

}
