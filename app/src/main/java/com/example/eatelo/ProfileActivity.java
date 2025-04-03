package com.example.eatelo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private EditText nameInput, bioInput;
    private Button nextButton;
    private String phone, password, name, bio;
    private ArrayList<String> preferences;
    private Uri imageUri; // Store selected image URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        profileImage = findViewById(R.id.profileImage);
        nameInput = findViewById(R.id.nameInput);
        bioInput = findViewById(R.id.bioInput);
        nextButton = findViewById(R.id.nextButton);

        // Retrieve user data from Intent
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        password = intent.getStringExtra("password");
        preferences = intent.getStringArrayListExtra("preferences");

        // Pre-fill the name field
        if (name != null && !name.isEmpty()) {
            nameInput.setText(name);
        }

        // Handle Image Selection
        profileImage.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, PICK_IMAGE);
        });

        // Handle Next Button Click
        nextButton.setOnClickListener(v -> {
            name = nameInput.getText().toString().trim();
            bio = bioInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert imageUri to string
            String imageUriString = (imageUri != null) ? imageUri.toString() : null;

            // Navigate to Ranking Page
            Intent rankingIntent = new Intent(ProfileActivity.this, RankingPageActivity.class);
            rankingIntent.putExtra("name", name);
            rankingIntent.putExtra("phone", phone);
            rankingIntent.putExtra("password", password);
            rankingIntent.putExtra("bio", bio);
            rankingIntent.putStringArrayListExtra("preferences", preferences);
            rankingIntent.putExtra("profileImageUri", imageUriString); // Pass image URI as string
            startActivity(rankingIntent);
        });
    }

    // Handle Image Selection Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
