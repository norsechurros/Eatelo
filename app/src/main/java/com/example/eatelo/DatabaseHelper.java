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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;
    private static final String DATABASE_NAME = "eatelo.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_RESTAURANTS = "restaurants";
    public static final String TABLE_PREFERENCES = "preferences";
    public static final String TABLE_RANKINGS = "rankings";
    public static final String TABLE_USER_RANKED_PAIRS = "user_ranked_pairs";

    // Common columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_NAME = "name";

    // Users table columns
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_BIO = "bio";
    public static final String COLUMN_PROFILE_PIC = "profile_pic";

    // Restaurants table columns
    public static final String COLUMN_RESTAURANT_NAME = "restaurant_name";
    public static final String COLUMN_RESTAURANT_ELO = "elo";
    public static final String COLUMN_ADDRESS = "address";

    // Preferences table columns
    public static final String COLUMN_PREFERENCE = "preferences";

    // Rankings table columns
    public static final String COLUMN_RANKINGS = "rankings";

    // User ranked pairs columns
    public static final String COLUMN_WINNER = "winner";
    public static final String COLUMN_LOSER = "loser";
    public static final String COLUMN_ELO_CHANGE_WINNER = "elo_change_winner";
    public static final String COLUMN_ELO_CHANGE_LOSER = "elo_change_loser";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables (same as before)
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PHONE + " TEXT UNIQUE, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_BIO + " TEXT DEFAULT NULL, " +
                COLUMN_PROFILE_PIC + " TEXT DEFAULT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_PREFERENCES + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_PREFERENCE + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_RANKINGS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_RANKINGS + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_RESTAURANTS + " (" +
                COLUMN_RESTAURANT_NAME + " TEXT UNIQUE, " +
                COLUMN_RESTAURANT_ELO + " INTEGER DEFAULT 1500, " +
                COLUMN_ADDRESS + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_USER_RANKED_PAIRS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER, " +
                "winner TEXT, " +
                "loser TEXT, " +
                "elo_change_winner INTEGER, " +
                "elo_change_loser INTEGER, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))");

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

        db.beginTransaction();
        try {
            for (String[] data : restaurantData) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_RESTAURANT_NAME, data[0]);
                values.put(COLUMN_RESTAURANT_ELO, 1200);
                values.put(COLUMN_ADDRESS, data[1]);
                db.insert(TABLE_RESTAURANTS, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_RANKED_PAIRS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RANKINGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        onCreate(db);
    }

    // User Management Methods
    // Update user profile information
    public boolean updateUserProfile(String phone, String name, String bio) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_BIO, bio);

            int rowsAffected = db.update(TABLE_USERS, values,
                    COLUMN_PHONE + " = ?", new String[]{phone});
            return rowsAffected > 0;
        } finally {
            db.close();
        }
    }

    // Get user's profile image
    public Bitmap getUserImage(String phone, Context context) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_PROFILE_PIC},
                COLUMN_PHONE + " = ?",
                new String[]{phone},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                String imagePath = cursor.getString(0);
                if (imagePath != null) {
                    try {
                        if (imagePath.startsWith("content://")) {
                            return MediaStore.Images.Media.getBitmap(
                                    context.getContentResolver(), Uri.parse(imagePath));
                        } else {
                            return BitmapFactory.decodeFile(imagePath);
                        }
                    } catch (Exception e) {
                        Log.e("DatabaseHelper", "Error loading user image", e);
                    }
                }
            }
            return BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_profile_placeholder);
        } finally {
            db.close();
        }
    }

    // Get user's name
    public String getUserName(String phone) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_NAME},
                COLUMN_PHONE + " = ?",
                new String[]{phone},
                null, null, null)) {

            return cursor.moveToFirst() ? cursor.getString(0) : "";
        } finally {
            db.close();
        }
    }

    // Get user's bio
    public String getUserBio(String phone) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_BIO},
                COLUMN_PHONE + " = ?",
                new String[]{phone},
                null, null, null)) {

            return cursor.moveToFirst() ? cursor.getString(0) : "";
        } finally {
            db.close();
        }
    }
    public long addUser(SQLiteDatabase db, String phone, String name, String password, String bio, String imagePath) {
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
            values.putNull(COLUMN_PROFILE_PIC);
        }

        return db.insert(TABLE_USERS, null, values);
    }

    public boolean validateUser(String phone, String password) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_PHONE + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{phone, password})) {
            return cursor.getCount() > 0;
        }
    }

    public long getUserId(SQLiteDatabase db, String phone) {
        try (Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_PHONE + " = ?",
                new String[]{phone})) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        }
        return -1;
    }

    // Restaurant Methods
    public ArrayList<String> getAllRestaurants() {
        ArrayList<String> restaurantList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT " + COLUMN_RESTAURANT_NAME + " FROM " + TABLE_RESTAURANTS, null)) {
            while (cursor.moveToNext()) {
                restaurantList.add(cursor.getString(0));
            }
        }
        return restaurantList;
    }

    public ArrayList<String> getAllRestaurantsWithAddresses() {
        ArrayList<String> restaurantList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT " + COLUMN_RESTAURANT_NAME + ", " + COLUMN_ADDRESS + " FROM " + TABLE_RESTAURANTS, null)) {
            while (cursor.moveToNext()) {
                restaurantList.add(cursor.getString(0) + " - " + cursor.getString(1));
            }
        }
        return restaurantList;
    }

    public int getRestaurantElo(SQLiteDatabase db, String restaurantName) {
        try (Cursor cursor = db.rawQuery("SELECT " + COLUMN_RESTAURANT_ELO + " FROM " + TABLE_RESTAURANTS +
                " WHERE " + COLUMN_RESTAURANT_NAME + " = ?", new String[]{restaurantName})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 1500; // Default ELO if not found
    }

    // Ranking Methods
    public void undoPreviousRanking(SQLiteDatabase db, long userId) {
        try (Cursor cursor = db.rawQuery("SELECT winner, loser, elo_change_winner, elo_change_loser FROM " +
                TABLE_USER_RANKED_PAIRS + " WHERE user_id = ?", new String[]{String.valueOf(userId)})) {

            db.beginTransaction();
            try {
                while (cursor.moveToNext()) {
                    String winner = cursor.getString(0);
                    String loser = cursor.getString(1);
                    int winnerChange = cursor.getInt(2);
                    int loserChange = cursor.getInt(3);

                    // Reverse the ELO changes
                    ContentValues winnerValues = new ContentValues();
                    winnerValues.put(COLUMN_RESTAURANT_ELO, getRestaurantElo(db, winner) - winnerChange);
                    db.update(TABLE_RESTAURANTS, winnerValues, COLUMN_RESTAURANT_NAME + " = ?", new String[]{winner});

                    ContentValues loserValues = new ContentValues();
                    loserValues.put(COLUMN_RESTAURANT_ELO, getRestaurantElo(db, loser) - loserChange);
                    db.update(TABLE_RESTAURANTS, loserValues, COLUMN_RESTAURANT_NAME + " = ?", new String[]{loser});
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        // Clear the ranked pairs
        db.delete(TABLE_USER_RANKED_PAIRS, "user_id = ?", new String[]{String.valueOf(userId)});
    }

    public void applyNewRanking(SQLiteDatabase db, long userId, ArrayList<String> rankedRestaurants) {
        ArrayList<String> allRestaurants = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("SELECT " + COLUMN_RESTAURANT_NAME + " FROM " + TABLE_RESTAURANTS, null)) {
            while (cursor.moveToNext()) {
                allRestaurants.add(cursor.getString(0));
            }
        }

        db.beginTransaction();
        try {
            // Compare each ranked restaurant with all unranked ones
            for (String rankedName : rankedRestaurants) {
                int rankedElo = getRestaurantElo(db, rankedName);

                for (String unrankedName : allRestaurants) {
                    if (!rankedRestaurants.contains(unrankedName)) {
                        int unrankedElo = getRestaurantElo(db, unrankedName);

                        // Ranked restaurant "wins" against unranked ones
                        EloCalculator.EloChange change = EloCalculator.calculateRatingChanges(rankedElo, unrankedElo);

                        // Update ELOs
                        ContentValues rankedValues = new ContentValues();
                        rankedValues.put(COLUMN_RESTAURANT_ELO, rankedElo + change.winnerChange);
                        db.update(TABLE_RESTAURANTS, rankedValues,
                                COLUMN_RESTAURANT_NAME + " = ?", new String[]{rankedName});

                        ContentValues unrankedValues = new ContentValues();
                        unrankedValues.put(COLUMN_RESTAURANT_ELO, unrankedElo + change.loserChange);
                        db.update(TABLE_RESTAURANTS, unrankedValues,
                                COLUMN_RESTAURANT_NAME + " = ?", new String[]{unrankedName});

                        // Store the pair
                        storeRankedPair(db, userId, rankedName, unrankedName,
                                change.winnerChange, change.loserChange);
                    }
                }
            }

            // Compare within ranked restaurants
            for (int i = 0; i < rankedRestaurants.size(); i++) {
                String higherRanked = rankedRestaurants.get(i);
                int higherElo = getRestaurantElo(db, higherRanked);

                for (int j = i + 1; j < rankedRestaurants.size(); j++) {
                    String lowerRanked = rankedRestaurants.get(j);
                    int lowerElo = getRestaurantElo(db, lowerRanked);

                    EloCalculator.EloChange change = EloCalculator.calculateRatingChanges(higherElo, lowerElo);

                    // Update ELOs
                    ContentValues higherValues = new ContentValues();
                    higherValues.put(COLUMN_RESTAURANT_ELO, higherElo + change.winnerChange);
                    db.update(TABLE_RESTAURANTS, higherValues,
                            COLUMN_RESTAURANT_NAME + " = ?", new String[]{higherRanked});

                    ContentValues lowerValues = new ContentValues();
                    lowerValues.put(COLUMN_RESTAURANT_ELO, lowerElo + change.loserChange);
                    db.update(TABLE_RESTAURANTS, lowerValues,
                            COLUMN_RESTAURANT_NAME + " = ?", new String[]{lowerRanked});

                    // Store the pair
                    storeRankedPair(db, userId, higherRanked, lowerRanked,
                            change.winnerChange, change.loserChange);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void storeRankedPair(SQLiteDatabase db, long userId, String winner, String loser,
                                 int eloChangeWinner, int eloChangeLoser) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("winner", winner);
        values.put("loser", loser);
        values.put("elo_change_winner", eloChangeWinner);
        values.put("elo_change_loser", eloChangeLoser);
        db.insert(TABLE_USER_RANKED_PAIRS, null, values);
    }

    // Other utility methods
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

        int rowsAffected = db.update(TABLE_RANKINGS, values, "user_id = ?",
                new String[]{String.valueOf(userId)});

        if (rowsAffected == 0) {
            db.insert(TABLE_RANKINGS, null, values);
        }
    }

    public SQLiteDatabase getWritableDatabaseWithRetry(int maxRetries) {
        SQLiteDatabase db = null;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                db = getWritableDatabase();
                break;
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw e;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return db;
    }

    /**
     * Add user preferences to database
     */
    public void addPreferences(SQLiteDatabase db, long userId, ArrayList<String> preferences) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_PREFERENCE, TextUtils.join(",", preferences));

        // Insert or replace existing preferences
        db.insertWithOnConflict(TABLE_PREFERENCES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Add user rankings to database
     */
    public void addRankings(SQLiteDatabase db, long userId, ArrayList<String> rankedRestaurants) {
        // Convert rankings to JSON format
        JSONObject rankingsJson = new JSONObject();
        for (int i = 0; i < rankedRestaurants.size(); i++) {
            try {
                rankingsJson.put(rankedRestaurants.get(i), i + 1); // Position as value
            } catch (JSONException e) {
                Log.e("DatabaseHelper", "Error creating rankings JSON", e);
            }
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_RANKINGS, rankingsJson.toString());

        // Insert or replace existing rankings
        db.insertWithOnConflict(TABLE_RANKINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Get user's rankings from database
     * @param db The database connection
     * @param userId The user ID to fetch rankings for
     * @return JSON string of rankings or null if not found
     */
    public String getRankings(SQLiteDatabase db, long userId) {
        try (Cursor cursor = db.query(
                TABLE_RANKINGS,
                new String[]{COLUMN_RANKINGS},
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }
        return null;
    }

    /**
     * Get restaurant details by name
     * @param db The database connection
     * @param restaurantName Name of restaurant to find
     * @return Restaurant object or null if not found
     */
    public Restaurant getRestaurantByName(SQLiteDatabase db, String restaurantName) {
        try (Cursor cursor = db.query(
                TABLE_RESTAURANTS,
                new String[]{COLUMN_RESTAURANT_NAME, COLUMN_RESTAURANT_ELO, COLUMN_ADDRESS},
                COLUMN_RESTAURANT_NAME + " = ?",
                new String[]{restaurantName},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                return new Restaurant(
                        cursor.getString(0),  // name
                        cursor.getInt(1),     // elo
                        cursor.getString(2)  // address
                );
            }
        }
        return null;
    }

    /**
     * Updates user preferences in the database
     * @param db The database connection
     * @param phone The user's phone number
     * @param updatedPreferences Comma-separated string of preferences
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserPreferences(SQLiteDatabase db, String phone, String updatedPreferences) {
        // First get the user ID
        long userId = getUserId(db, phone);
        if (userId == -1) {
            return false; // User not found
        }

        ContentValues values = new ContentValues();
        values.put("preferences", updatedPreferences);

        // Try to update existing record
        int rowsAffected = db.update(TABLE_PREFERENCES,
                values,
                "user_id = ?",
                new String[]{String.valueOf(userId)});

        // If no existing record, insert new one
        if (rowsAffected == 0) {
            values.put("user_id", userId);
            long result = db.insert(TABLE_PREFERENCES, null, values);
            return result != -1;
        }

        return rowsAffected > 0;
    }
}