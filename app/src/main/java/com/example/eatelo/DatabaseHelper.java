package com.example.eatelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

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

    // Users Table Columns
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";

    // Preferences Table Columns
    private static final String COLUMN_PREFERENCE_ID = "id";
    private static final String COLUMN_USER_ID_FK = "user_id";
    private static final String COLUMN_PREFERENCE = "preference";

    // Rankings Table Columns
    private static final String COLUMN_RANKING_ID = "id";
    private static final String COLUMN_RESTAURANT_NAME = "restaurant_name";
    private static final String COLUMN_RANK = "rank";

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
                + "password TEXT)";
        db.execSQL(createUsersTable);

        // Create Preferences Table (Now Storing CSV)
        String createPreferencesTable = "CREATE TABLE preferences ("
                + "user_id INTEGER PRIMARY KEY, "
                + "preferences TEXT, "
                + "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(createPreferencesTable);

        // Create Rankings Table (Now Storing JSON)
        String createRankingsTable = "CREATE TABLE rankings ("
                + "user_id INTEGER PRIMARY KEY, "
                + "rankings TEXT, "
                + "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(createRankingsTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RANKINGS);
        onCreate(db);
    }

    // **Function to add a new user**
    public long addUser(String phone, String name, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PASSWORD, password);

        long userId = db.insert(TABLE_USERS, null, values);
        db.close();
        return userId; // Returns the user ID if successful, -1 if failed
    }

    public boolean validateUser(String phone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phone=? AND password=?", new String[]{phone, password});

        boolean userExists = cursor.getCount() > 0; // If user exists, count > 0
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
            preferencesList.addAll(Arrays.asList(preferencesString.split(","))); // Convert CSV back to List
        }

        cursor.close();
        db.close();
        return preferencesList;
    }

    public HashMap<String, Integer> getRankings(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT rankings FROM rankings WHERE user_id=?", new String[]{String.valueOf(userId)});

        HashMap<String, Integer> rankingsMap = new HashMap<>();
        if (cursor.moveToFirst()) {
            try {
                JSONObject rankingsJson = new JSONObject(cursor.getString(0));
                Iterator<String> keys = rankingsJson.keys();
                while (keys.hasNext()) {
                    String restaurant = keys.next();
                    rankingsMap.put(restaurant, rankingsJson.getInt(restaurant));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        db.close();
        return rankingsMap;
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
