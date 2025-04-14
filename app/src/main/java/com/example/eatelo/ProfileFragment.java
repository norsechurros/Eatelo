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

    // Constants for validation
    private static final int MAX_NAME_LENGTH = 20;
    private static final int MAX_BIO_LENGTH = 40;
    private static final int MIN_NAME_LENGTH = 2;

    // UI components
    private ImageView profileImage;
    private EditText nameEditText, bioEditText;
    private TextView preferencesTextView, rankingsTextView;
    private Button saveChangesButton, updatePreferencesButton, updateRankingsButton;

    // Data fields
    private String userPhone;
    private DatabaseHelper dbHelper;

    public ProfileFragment() {
        // Required empty public constructor
    }

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
        dbHelper = DatabaseHelper.getInstance(requireContext());
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

        if (!validateInputs(updatedName, updatedBio)) {
            return;
        }

        boolean updateSuccess = dbHelper.updateUserProfile(userPhone, updatedName, updatedBio);
        if (updateSuccess) {
            showToast("Profile updated successfully");
        } else {
            showToast("Failed to update profile");
        }
    }

    private boolean validateInputs(String name, String bio) {
        if (name.isEmpty()) {
            showToast("Please enter your name");
            return false;
        }

        if (name.length() < MIN_NAME_LENGTH) {
            showToast("Name must be at least " + MIN_NAME_LENGTH + " characters");
            return false;
        }

        if (name.length() > MAX_NAME_LENGTH) {
            showToast("Name cannot exceed " + MAX_NAME_LENGTH + " characters");
            return false;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            showToast("Name can only contain letters and spaces");
            return false;
        }

        if (bio.length() > MAX_BIO_LENGTH) {
            showToast("Bio cannot exceed " + MAX_BIO_LENGTH + " characters");
            return false;
        }

        return true;
    }

    private void openUpdatePreferences() {
        if (userPhone == null) {
            showToast("User information not available");
            return;
        }
        startActivity(new Intent(requireContext(), UpdatePreferencesActivity.class)
                .putExtra("phone", userPhone));
    }

    private void openUpdateRankings() {
        if (userPhone == null) {
            showToast("User information not available");
            return;
        }
        startActivity(new Intent(requireContext(), UpdateRankingsActivity.class)
                .putExtra("phone", userPhone));
    }

    private void loadUserProfile() {
        if (userPhone == null) {
            showToast("User information not available");
            return;
        }

        try {
            Context context = requireContext();
            Bitmap userImage = dbHelper.getUserImage(userPhone, context);
            profileImage.setImageBitmap(userImage != null ? userImage :
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_placeholder));

            nameEditText.setText(dbHelper.getUserName(userPhone));
            bioEditText.setText(dbHelper.getUserBio(userPhone));
        } catch (Exception e) {
            showToast("Error loading profile");
            e.printStackTrace();
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Note: We're not closing dbHelper as it's a singleton
    }
}