package com.example.eatelo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        dbHelper = DatabaseHelper.getInstance(requireContext()); // Fixed: Using singleton
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeViews(view);
        loadUserProfile();
        setupButtonListeners();

        return view;
    }

    private void initializeViews(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        nameEditText = view.findViewById(R.id.nameInput);
        bioEditText = view.findViewById(R.id.bioInput);
        saveChangesButton = view.findViewById(R.id.saveButton);
        updatePreferencesButton = view.findViewById(R.id.updatePreferencesButton);
        updateRankingsButton = view.findViewById(R.id.updateRankingButton);
    }

    private void setupButtonListeners() {
        saveChangesButton.setOnClickListener(v -> saveProfileChanges());
        updatePreferencesButton.setOnClickListener(v -> openUpdatePreferences());
        updateRankingsButton.setOnClickListener(v -> openUpdateRankings());
    }

    private void saveProfileChanges() {
        String updatedName = nameEditText.getText().toString().trim();
        String updatedBio = bioEditText.getText().toString().trim();

        if (updatedName.isEmpty()) {
            showToast("Please enter your name");
            return;
        }

        dbHelper.updateUserProfile(userPhone, updatedName, updatedBio);
        showToast("Profile updated successfully");
    }

    private void openUpdatePreferences() {
        startActivity(new Intent(requireContext(), UpdatePreferencesActivity.class)
                .putExtra("phone", userPhone));
    }

    private void openUpdateRankings() {
        startActivity(new Intent(requireContext(), UpdateRankingsActivity.class)
                .putExtra("phone", userPhone));
    }

    private void loadUserProfile() {
        if (userPhone == null) return;

        Context context = requireContext();
        Bitmap userImage = dbHelper.getUserImage(userPhone, context);
        profileImage.setImageBitmap(userImage != null ? userImage :
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_placeholder));

        nameEditText.setText(dbHelper.getUserName(userPhone));
        bioEditText.setText(dbHelper.getUserBio(userPhone));
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // No need to close dbHelper - singleton manages its own lifecycle
    }
}