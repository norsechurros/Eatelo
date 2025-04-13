package com.example.eatelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "eatelo.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PREFERENCES = "preferences";
    private static final String TABLE_RANKINGS = "rankings";
    private static final String TABLE_RESTAURANTS = "restaurants"; // New table

    // Users Table Columns
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_BIO = "bio";
    private static final String COLUMN_PROFILE_PIC = "profile_pic"; // Store as String (file path)


    // Preferences Table Columns
    private static final String COLUMN_PREFERENCE_ID = "id";
    private static final String COLUMN_USER_ID_FK = "user_id";
    private static final String COLUMN_PREFERENCE = "preference";

    // Rankings Table Columns
    private static final String COLUMN_RANKING_ID = "id";
    private static final String COLUMN_RESTAURANT_NAME = "restaurant_name";
    private static final String COLUMN_RANK = "rank";

    // Restaurants Table Columns
    private static final String COLUMN_RESTAURANT_ID = "id";
    private static final String COLUMN_RESTAURANT_ELO = "elo";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String createUsersTable = "CREATE TABLE users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "phone TEXT UNIQUE, "
                + "name TEXT, "
                + "password TEXT, "
                + "bio TEXT DEFAULT NULL, "
                + "profile_pic TEXT DEFAULT NULL)";
        db.execSQL(createUsersTable);

        // Create Preferences Table
        String createPreferencesTable = "CREATE TABLE preferences ("
                + "user_id INTEGER PRIMARY KEY, "
                + "preferences TEXT, "
                + "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(createPreferencesTable);

        // Create Rankings Table
        String createRankingsTable = "CREATE TABLE rankings ("
                + "user_id INTEGER PRIMARY KEY, "
                + "rankings TEXT, "
                + "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(createRankingsTable);

        // Create Restaurants Table
        String createRestaurantsTable = "CREATE TABLE restaurants ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "restaurant_name TEXT UNIQUE, "
                + "elo INTEGER DEFAULT 1500, "
                + "address TEXT)";

        db.execSQL(createRestaurantsTable);

        // Prepopulate Restaurants
        prepopulateRestaurants(db);
    }

    private void prepopulateRestaurants(SQLiteDatabase db) {
        String[][] restaurantData = {
                {"Eye of the Tiger", "MIT Road, Manipal"},
                {"Dollops", "End Point Road, Manipal"},
                {"Basil Cafe", "KMC Greens, Manipal"},
                {"Country Inn", "NH 169A, Manipal"},
                {"Hadiqa", "Near MIT Backgate, Manipal"},
                {"Saibas", "Indrali, Udupi"},
                {"Snack Shack", "Tiger Circle, Manipal"},
                {"Madhuvan Serai", "KMC Road, Manipal"},
                {"Rolls Mania", "End Point Road, Manipal"},
                {"Bacchus Inn", "End Point Road, Manipal"},
                {"Laughing Buddha", "Kadiyali, Udupi"},
                {"The Belgian Waffle Co.", "Tiger Circle, Manipal"},
                {"Tiwari Chat", "Tiger Circle, Manipal"},
                {"Hungry House", "Ananth Nagar, Manipal"},
                {"Guzzlers Inn", "Tiger Circle, Manipal"},
                {"Hotel Manipal Restaurant", "Main Road, Manipal"},
                {"Arabian Tasty", "End Point Road, Manipal"},
                {"Smoked BBQ Taxi", "End Point Road, Manipal"},
                {"Kahani", "End Point Road, Manipal"},
                {"Mallika", "Udupi Main Road"}
        };

        for (String[] data : restaurantData) {
            ContentValues values = new ContentValues();
            values.put("restaurant_name", data[0]);
            values.put("elo", 1200);
            values.put("address", data[1]);
            db.insert(TABLE_RESTAURANTS, null, values);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RANKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANTS);
        onCreate(db);
    }

    public ArrayList<String> getAllRestaurantsWithAddresses() {
        ArrayList<String> restaurantList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT restaurant_name, address FROM restaurants", null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String address = cursor.getString(1);
                restaurantList.add(name + " - " + address);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return restaurantList;
    }


    // Fetch all restaurant names from the database
    public ArrayList<String> getAllRestaurants() {
        ArrayList<String> restaurantList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT restaurant_name FROM restaurants", null);

        if (cursor.moveToFirst()) {
            do {
                restaurantList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return restaurantList;
    }



    // Modify DatabaseHelper to store image path
    public long addUser(String phone, String name, String password, String bio, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PASSWORD, password);

        if (bio != null && !bio.trim().isEmpty()) {
            values.put(COLUMN_BIO, bio);
        } else {
            values.putNull(COLUMN_BIO);
        }

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            values.put(COLUMN_PROFILE_PIC, imagePath);
        } else {
            values.putNull(COLUMN_PROFILE_PIC); // Store NULL if no image selected
        }

        long userId = db.insert(TABLE_USERS, null, values);
        db.close();
        return userId;
    }
    public void updateUserProfile(String phone, String name, String bio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_BIO, bio);
        db.update(TABLE_USERS, values, "phone = ?", new String[]{phone});
        db.close();
    }

    public String getUserName(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM users WHERE phone = ?", new String[]{phone});
        String name = null;
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return name;
    }
    public long getUserId(SQLiteDatabase db, String phone) {
        long userId = -1;
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE phone = ?", new String[]{phone});
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getLong(0);
            cursor.close();
        }
        return userId;
    }


    public boolean updateUserPreferences(String phone, String updatedPreferences) {
        SQLiteDatabase db = this.getWritableDatabase(); // Open database for write

        // Get user ID first, pass db to getUserId
        long userId = getUserId(db, phone);
        if (userId == -1) {
            Log.e("DB", "User not found for phone: " + phone);
            db.close(); // Don't forget to close DB when done
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("preferences", updatedPreferences);

        int rowsAffected = db.update("preferences", values, "user_id = ?", new String[]{String.valueOf(userId)});

        // Insert if no rows were affected
        if (rowsAffected == 0) {
            values.put("user_id", userId);
            db.insert("preferences", null, values);
        }

        db.close(); // Close database after operation
        return true; // Successfully updated
    }



    public void updateRankings(SQLiteDatabase db, long userId, ArrayList<String> rankedRestaurants) {
        JSONObject rankingsJson = new JSONObject();
        for (int i = 0; i < rankedRestaurants.size(); i++) {
            try {
                rankingsJson.put(rankedRestaurants.get(i), i + 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("rankings", rankingsJson.toString());

        int rowsAffected = db.update("rankings", values, "user_id = ?", new String[]{String.valueOf(userId)});

        // Optional: Insert if no rows were affected
        if (rowsAffected == 0) {
            db.insert("rankings", null, values);
        }
    }

    public int getRestaurantElo(String restaurantName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT elo FROM restaurants WHERE restaurant_name = ?", new String[]{restaurantName});

        int elo = -1;
        if (cursor.moveToFirst()) {
            elo = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return elo;
    }


    public Bitmap getUserImage(String phone, Context context) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT profile_pic FROM users WHERE phone = ?", new String[]{phone});
        if (cursor.moveToFirst()) {
            String path = cursor.getString(0);  // can be either file path or content URI
            cursor.close();

            try {
                if (path.startsWith("content://")) {
                    return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(path));
                } else {
                    // Treat it as a file path
                    return BitmapFactory.decodeFile(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_profile_placeholder); // fallback
            }
        }
        cursor.close();
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_profile_placeholder);
    }


    public String getUserBio(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT bio FROM " + TABLE_USERS + " WHERE phone = ?", new String[]{phone});

        if (cursor.moveToFirst()) {
            String bio = cursor.getString(0);
            cursor.close();
            return bio != null ? bio : "";  // Return empty string if bio is NULL
        }

        cursor.close();
        return "";
    }


    public boolean validateUser(String phone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phone=? AND password=?", new String[]{phone, password});

        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return userExists;
    }

    // **Function to add user preferences**
    public void addPreferences(long userId, ArrayList<String> preferences) {
        SQLiteDatabase db = this.getWritableDatabase();
        String preferencesString = TextUtils.join(",", preferences); // Convert to CSV format

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("preferences", preferencesString);

        db.insertWithOnConflict("preferences", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // **Function to add user rankings**
    public void addRankings(long userId, ArrayList<String> rankedRestaurants) {
        SQLiteDatabase db = this.getWritableDatabase();

        JSONObject rankingsJson = new JSONObject();
        for (int i = 0; i < rankedRestaurants.size(); i++) {
            try {
                rankingsJson.put(rankedRestaurants.get(i), i + 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("rankings", rankingsJson.toString()); // Store JSON as a string

        db.insertWithOnConflict("rankings", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public ArrayList<String> getPreferences(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT preferences FROM preferences WHERE user_id=?", new String[]{String.valueOf(userId)});

        ArrayList<String> preferencesList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            String preferencesString = cursor.getString(0);
            preferencesList.addAll(Arrays.asList(preferencesString.split(",")));
        }

        cursor.close();
        db.close();
        return preferencesList;
    }
    public long getUserIdFromPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE phone = ?", new String[]{phone});
        long userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    public String getRankings(SQLiteDatabase db, long userId) {
        Cursor cursor = db.rawQuery("SELECT rankings FROM rankings WHERE user_id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("rankings"));
        }
        cursor.close();
        return null; // Return null if rankings are not found
    }
    public Restaurant getRestaurantByName(SQLiteDatabase db, String restaurantName) {
        Cursor cursor = db.rawQuery("SELECT restaurant_name, elo, address FROM restaurants WHERE restaurant_name = ?", new String[]{restaurantName});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("restaurant_name"));
            int elo = cursor.getInt(cursor.getColumnIndex("elo"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            cursor.close();
            return new Restaurant(name, elo, address); // Return restaurant object
        }
        cursor.close();
        return null; // Return null if restaurant not found
    }
    // **Function to check if a user exists by phone**
    public boolean userExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_PHONE + "=?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
}
