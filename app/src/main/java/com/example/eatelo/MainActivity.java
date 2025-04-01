package com.example.eatelo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declare the rectangle view
    private View rectangleView;

    private float dY;
    private float rectanglePreviousY;
    private boolean isMoving = false;

    // Height threshold to trigger transition from the bottom (e.g., 200 pixels from the bottom)
    private static final float HEIGHT_THRESHOLD_FROM_BOTTOM = 900;

    // Flag to check if transition is triggered
    private boolean transitionTriggered = false;

    // Store the initial Y position of the rectangle
    private float initialRectangleY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the rectangle view
        rectangleView = findViewById(R.id.rectangle);

        // Store the initial Y position of the rectangle to avoid going below it
        initialRectangleY = 2050;
        // Set onTouchListener to handle dragging for the rectangle view
        rectangleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleTouchEvent(event);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset the rectangle position when coming back from the second activity
        resetRectanglePosition();
    }

    private void resetRectanglePosition() {
        // Reset the position of the rectangle to its initial Y position
        rectangleView.setY(initialRectangleY);
        transitionTriggered = false; // Reset the transition flag
    }

    private boolean handleTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Record the initial touch position
                dY = event.getRawY() - rectangleView.getY();
                rectanglePreviousY = rectangleView.getY();
                isMoving = true;
                return true;

            case MotionEvent.ACTION_MOVE:
                if (isMoving) {
                    // Calculate the movement delta based on the user's touch
                    float newY = event.getRawY() - dY;

                    // Prevent the rectangle from going below its initial position
                    if (newY > initialRectangleY) {
                        newY = initialRectangleY; // Limit the Y position to the initial position
                    }

                    // Move the rectangle view relative to its previous Y position
                    rectangleView.setY(newY);

                    // Update the previous Y position for future movements
                    rectanglePreviousY = rectangleView.getY();

                    // Check if the rectangle has moved past the threshold from the bottom
                    float screenHeight = getResources().getDisplayMetrics().heightPixels;
                    float rectY = rectangleView.getY();
                    float distanceFromBottom = screenHeight - rectY;

                    // Trigger the transition once the threshold is passed
                    if (distanceFromBottom >= HEIGHT_THRESHOLD_FROM_BOTTOM && !transitionTriggered) {
                        transitionTriggered = true; // Mark that the transition should happen
                        transitionToNextActivity(); // Perform the transition
                    }

                    return true;
                }

            case MotionEvent.ACTION_UP:
                // Stop moving when the touch is released
                isMoving = false;

                // If the threshold is passed, transition to the next screen (already triggered in ACTION_MOVE)
                return true;

            default:
                return false;
        }
    }

    // Method to handle activity transition when rectangle reaches the threshold from the bottom
    private void transitionToNextActivity() {
        // Start the second activity (GettingStarted)
        Intent intent = new Intent(MainActivity.this, GettingStarted.class);
        startActivity(intent);
    }

}
