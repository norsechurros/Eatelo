package com.example.eatelo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int CAPTURE_IMAGE = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int MAX_NAME_LENGTH = 20;
    private static final int MAX_BIO_LENGTH = 40;

    private ImageView profileImage;
    private EditText nameInput, bioInput;
    private Button nextButton;
    private String phone, password, name, bio;
    private ArrayList<String> preferences;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        getIntentData();
        setupClickListeners();
    }

    private void initializeViews() {
        profileImage = findViewById(R.id.profileImage);
        nameInput = findViewById(R.id.nameInput);
        bioInput = findViewById(R.id.bioInput);
        nextButton = findViewById(R.id.nextButton);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        password = intent.getStringExtra("password");
        preferences = intent.getStringArrayListExtra("preferences");

        if (name != null && !name.isEmpty()) {
            nameInput.setText(name);
        }
    }

    private void setupClickListeners() {
        profileImage.setOnClickListener(v -> showImagePickDialog());

        nextButton.setOnClickListener(v -> {
            if (validateInputs()) {
                proceedToRanking();
            }
        });
    }

    private boolean validateInputs() {
        name = nameInput.getText().toString().trim();
        bio = bioInput.getText().toString().trim();

        if (name.isEmpty()) {
            showToast("Please enter your name");
            return false;
        }

        if (name.length() > MAX_NAME_LENGTH) {
            showToast("Name should not exceed " + MAX_NAME_LENGTH + " characters");
            return false;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            showToast("Name should contain only letters and spaces");
            return false;
        }

        if (bio.length() > MAX_BIO_LENGTH) {
            showToast("Bio should not exceed " + MAX_BIO_LENGTH + " characters");
            return false;
        }

        return true;
    }

    private void proceedToRanking() {
        String imageUriString = (imageUri != null) ? imageUri.toString() : null;

        Intent rankingIntent = new Intent(this, RankingPageActivity.class);
        rankingIntent.putExtra("name", name);
        rankingIntent.putExtra("phone", phone);
        rankingIntent.putExtra("password", password);
        rankingIntent.putExtra("bio", bio);
        rankingIntent.putStringArrayListExtra("preferences", preferences);
        rankingIntent.putExtra("profileImageUri", imageUriString);
        startActivity(rankingIntent);
    }

    private void showImagePickDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Select Profile Picture")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        handleCameraOption();
                    } else {
                        handleGalleryOption();
                    }
                })
                .show();
    }

    private void handleCameraOption() {
        if (checkCameraPermission()) {
            openCamera();
        } else {
            requestCameraPermission();
        }
    }

    private void handleGalleryOption() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto, PICK_IMAGE);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                CAMERA_PERMISSION_CODE);
    }

    private void openCamera() {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Profile Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Taken from camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, CAPTURE_IMAGE);
        } catch (Exception e) {
            showToast("Failed to open camera");
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                showToast("Camera & Storage permissions are required");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == PICK_IMAGE && data != null) {
                    imageUri = data.getData();
                }

                if (imageUri != null) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    profileImage.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                showToast("Failed to load image");
                e.printStackTrace();
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}