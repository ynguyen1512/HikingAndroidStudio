package com.example.khike;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "hike.db";
    public static final Integer DATABASE_VERSION = 2;

    public static final String TABLE_USER = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_FULL_NAME = "user_fullname";
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_USER_PASSWORD = "user_password";

    public static final String TABLE_HIKE_NAME = "hikes";
    public static final String COLUMN_HIKE_IMAGE = "hike_image";
    public static final String COLUMN_HIKE_ID = "hike_id";
    public static final String COLUMN_HIKE_NAME = "hike_name";
    public static final String COLUMN_HIKE_LOCATION = "hike_location";
    public static final String COLUMN_HIKE_DATE = "hike_date";
    public static final String COLUMN_HIKE_PARKING = "hike_parking";
    public static final String COLUMN_HIKE_LENGTH = "hike_length";
    public static final String COLUMN_HIKE_DIFFICULTY = "hike_difficulty";
    public static final String COLUMN_HIKE_DESCRIPTION = "hike_description";

    public static final String TABLE_OBSERVATIONS = "observations";
    public static final String COLUMN_OBSERVATION_ID = "observation_id";
    public static final String COLUMN_OBSERVATION_NAME = "observation_name";
    public static final String COLUMN_OBSERVATION_DATE = "observation_date";
    private static final String COLUMN_OBSERVATION_TIME = "observation_time";
    private static final String COLUMN_OBSERVATION_NOTES = "observation_notes";
    public static final String COLUMN_OBSERVATION_IMAGE = "observation_image";

    public static final String TABLE_EXPLORE = "explore";
    public static final String COLUMN_EXPLORE_ID = "explore_id";
    public static final String COLUMN_EXPLORE_NAME = "explore_name";
    public static final String COLUMN_EXPLORE_LOCATION = "explore_location";
    public static final String COLUMN_EXPLORE_DATE = "explore_date";
    private static final String COLUMN_EXPLORE_IMAGE = "explore_image";


    public static final String TABLE_BOOKMARKED_HIKE = "BookmarkedHike";
    public static final String COLUMN_ID_BOOKMARKED = "bookmarked_id";
    public static final String COLUMN_NAME_BOOKMARKED = "bookmarked_name";
    public static final String COLUMN_LOCATION_BOOKMARKED = "bookmarked_location";
    public static final String COLUMN_DATE_BOOKMARKED = "bookmarked_date";
    public static final String COLUMN_PARKING_BOOKMARKED = "bookmarked_parking";
    public static final String COLUMN_DIFFICULTY_BOOKMARKED = "bookmarked_difficulty";
    public static final String COLUMN_LENGTH_BOOKMARKED = "bookmarked_length";
    public static final String COLUMN_DESCRIPTION_BOOKMARKED = "bookmarked_description";
    public static final String COLUMN_IMAGE_BOOKMARKED = "bookmarked_image";

    private final Context context;
    public DBHelper(@Nullable Context context) {

        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private static final String TABLE_USER_CREATE =
            "CREATE TABLE " + TABLE_USER + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FULL_NAME + " TEXT, " +
                    COLUMN_USER_NAME + " TEXT, " +
                    COLUMN_USER_PASSWORD + " TEXT" + ")";
    private static final String TABLE_HIKE_CREATE =
            "CREATE TABLE " + TABLE_HIKE_NAME + " (" +
                    COLUMN_HIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_HIKE_NAME + " TEXT, " +
                    COLUMN_HIKE_LOCATION + " TEXT, " +
                    COLUMN_HIKE_DATE + " TEXT, " +
                    COLUMN_HIKE_PARKING + " TEXT, " +
                    COLUMN_HIKE_LENGTH + " TEXT, " +
                    COLUMN_HIKE_DIFFICULTY + " TEXT, " +
                    COLUMN_HIKE_DESCRIPTION + " TEXT, " +
                    COLUMN_HIKE_IMAGE + " BLOB," +
                    "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")" + ")";


    private static final String TABLE_OBSERVATION_CREATE =
            "CREATE TABLE " + TABLE_OBSERVATIONS + " (" +
                    COLUMN_OBSERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_HIKE_ID + " INTEGER, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_OBSERVATION_NAME + " TEXT, " +
                    COLUMN_OBSERVATION_DATE + " TEXT, " +
                    COLUMN_OBSERVATION_TIME + " TEXT," +
                    COLUMN_OBSERVATION_NOTES + " TEXT, " +
                    COLUMN_OBSERVATION_IMAGE + " BLOB" + ")";


    private static final String TABLE_COMMUNITY_CREATE =
            "CREATE TABLE " + TABLE_EXPLORE + " (" +
                    COLUMN_EXPLORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EXPLORE_NAME + " TEXT, " +
                    COLUMN_EXPLORE_LOCATION + " TEXT, " +
                    COLUMN_EXPLORE_DATE + " TEXT, " +
                    COLUMN_EXPLORE_IMAGE + " BLOB" + ")";


    private static final String DATABASE_BOOKMARKED_CREATE =
            "CREATE TABLE " + TABLE_BOOKMARKED_HIKE + " (" +
                    COLUMN_ID_BOOKMARKED + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                    COLUMN_NAME_BOOKMARKED + " TEXT, " +
                    COLUMN_LOCATION_BOOKMARKED + " TEXT, " +
                    COLUMN_DATE_BOOKMARKED + " TEXT, " +
                    COLUMN_PARKING_BOOKMARKED + " TEXT, " +
                    COLUMN_LENGTH_BOOKMARKED + " TEXT, " +
                    COLUMN_DIFFICULTY_BOOKMARKED + " TEXT, " +
                    COLUMN_DESCRIPTION_BOOKMARKED + " TEXT, " +
                    COLUMN_IMAGE_BOOKMARKED + " TEXT " + ")";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_USER_CREATE);
        sqLiteDatabase.execSQL(TABLE_HIKE_CREATE);
        sqLiteDatabase.execSQL(TABLE_OBSERVATION_CREATE);
        sqLiteDatabase.execSQL(TABLE_COMMUNITY_CREATE);
        sqLiteDatabase.execSQL(DATABASE_BOOKMARKED_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPLORE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKED_HIKE);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    public boolean insertUser(String fullname, String username, String password) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FULL_NAME, fullname);
        contentValues.put(COLUMN_USER_NAME, username);
        contentValues.put(COLUMN_USER_PASSWORD, password);
        long result = myDB.insert(TABLE_USER, null, contentValues);
        return result != -1;
    }

    public boolean updateFullNameAndUserName(int userId, String newFullName, String newUserName, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_FULL_NAME, newFullName);
        cv.put(COLUMN_USER_NAME, newUserName);

        int result = db.update(TABLE_USER, cv, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        return result > 0;
    }

    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        return res;
    }

    public String getPasswordForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Ensure there are spaces before and after table/column names.
        String selectQuery = "SELECT " + COLUMN_USER_PASSWORD + " FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(userId) });

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD));
            cursor.close();
            return password;
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }

    @SuppressLint("Range")
    public int getUserIdByName(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Using cursor to fetch the result from the database.
        Cursor cursor = null;

        int userId = -1;

        try {
            // Fetches the COLUMN_USER_ID from the TABLE_USER where the COLUMN_USER_NAME matches the given username.
            cursor = db.query(TABLE_USER,
                    new String[]{COLUMN_USER_ID},
                    COLUMN_USER_NAME + "=?",
                    new String[]{userName},
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userId;
    }

    public boolean updatePassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USER_PASSWORD, newPassword);

        int result = db.update(TABLE_USER, cv, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        return result > 0;
    }


    public boolean checkUsername(String username) {
        SQLiteDatabase myDB = this.getReadableDatabase();
        String selection = COLUMN_USER_NAME + " = ?";
        String[] selectionArgs = { username };

        try (Cursor cursor = myDB.query(TABLE_USER, null, selection, selectionArgs, null, null, null)) {
            return cursor.getCount() > 0;
        }
    }



    public boolean checkUserPass(String username, String password) {
        SQLiteDatabase myDB = this.getReadableDatabase();
        String selection = COLUMN_USER_NAME + " = ?";
        String[] selectionArgs = { username };

        try (Cursor cursor = myDB.query(TABLE_USER, new String[]{COLUMN_USER_PASSWORD}, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String storedPassword = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD));
                return password.equals(storedPassword);
            }
            return false;
        }
    }


    public void addHiking(String name, String location, String date, String parking, String length,
                        String difficulty, String description, Bitmap hikeImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_HIKE_NAME, name);
        values.put(COLUMN_HIKE_LOCATION, location);
        values.put(COLUMN_HIKE_DATE, date);
        values.put(COLUMN_HIKE_PARKING, parking);
        values.put(COLUMN_HIKE_LENGTH, length);
        values.put(COLUMN_HIKE_DIFFICULTY, difficulty);
        values.put(COLUMN_HIKE_DESCRIPTION, description);

        // Convert the Bitmap into a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        hikeImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        values.put(COLUMN_HIKE_IMAGE, imageBytes);

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        db.insert(TABLE_HIKE_NAME, null, values);
        db.close();
    }

    public List<HikeData> getAllHiking() {
        List<HikeData> hikeDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_HIKE_ID, COLUMN_HIKE_NAME, COLUMN_HIKE_LOCATION, COLUMN_HIKE_DATE,
                COLUMN_HIKE_PARKING, COLUMN_HIKE_LENGTH, COLUMN_HIKE_DIFFICULTY, COLUMN_HIKE_DESCRIPTION, COLUMN_HIKE_IMAGE
        };

        Cursor cursor = db.query(
                TABLE_HIKE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_HIKE_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_HIKE_NAME));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex(COLUMN_HIKE_LOCATION));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_HIKE_DATE));
                @SuppressLint("Range") String parking = cursor.getString(cursor.getColumnIndex(COLUMN_HIKE_PARKING));
                @SuppressLint("Range") String length = cursor.getString(cursor.getColumnIndex(COLUMN_HIKE_LENGTH));
                @SuppressLint("Range") String difficulty = cursor.getString(cursor.getColumnIndex(COLUMN_HIKE_DIFFICULTY));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_HIKE_DESCRIPTION));
                @SuppressLint("Range") byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_HIKE_IMAGE));
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                HikeData hikeData = new HikeData(id, name, location, date, parking, length, difficulty, description, imageBitmap);
                hikeDataList.add(hikeData);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return hikeDataList;
    }

    public HikeData getHikeById(int hikeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_HIKE_ID, COLUMN_HIKE_NAME, COLUMN_HIKE_LOCATION, COLUMN_HIKE_DATE,
                COLUMN_HIKE_PARKING, COLUMN_HIKE_LENGTH, COLUMN_HIKE_DIFFICULTY, COLUMN_HIKE_DESCRIPTION, COLUMN_HIKE_IMAGE
        };

        String selection = COLUMN_HIKE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(hikeId)};

        Cursor cursor = db.query(
                TABLE_HIKE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        HikeData hikeData = null;

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_HIKE_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_HIKE_NAME);
            int locationIndex = cursor.getColumnIndex(COLUMN_HIKE_LOCATION);
            int dateIndex = cursor.getColumnIndex(COLUMN_HIKE_DATE);
            int parkingIndex = cursor.getColumnIndex(COLUMN_HIKE_PARKING);
            int lengthIndex = cursor.getColumnIndex(COLUMN_HIKE_LENGTH);
            int difficultyIndex = cursor.getColumnIndex(COLUMN_HIKE_DIFFICULTY);
            int descriptionIndex = cursor.getColumnIndex(COLUMN_HIKE_DESCRIPTION);
            int imageIndex = cursor.getColumnIndex(COLUMN_HIKE_IMAGE);

            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            String location = cursor.getString(locationIndex);
            String date = cursor.getString(dateIndex);
            String parking = cursor.getString(parkingIndex);
            String length = cursor.getString(lengthIndex);
            String difficulty = cursor.getString(difficultyIndex);
            String description = cursor.getString(descriptionIndex);

            byte[] imageBytes = cursor.getBlob(imageIndex);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            hikeData = new HikeData(id, name, location, date, parking, length, difficulty, description, imageBitmap);

            cursor.close();
        }

        db.close();

        return hikeData;
    }



    public boolean updateHike(int hikeId, HikeData hikeData) {
        // Get the database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_HIKE_NAME, hikeData.getName());
        contentValues.put(COLUMN_HIKE_LOCATION, hikeData.getLocation());
        contentValues.put(COLUMN_HIKE_DIFFICULTY, hikeData.getDifficulty());
        contentValues.put(COLUMN_HIKE_LENGTH, hikeData.getLength());
        contentValues.put(COLUMN_HIKE_PARKING, hikeData.getParking());
        contentValues.put(COLUMN_HIKE_DATE, hikeData.getDate());
        contentValues.put(COLUMN_HIKE_DESCRIPTION, hikeData.getDescription());
        contentValues.put(COLUMN_HIKE_IMAGE, getBytesFromBitmap(hikeData.getImageBitmap()));

        // Update the database
        int numberOfRowsAffected = db.update(TABLE_HIKE_NAME, contentValues, COLUMN_HIKE_ID + "=?", new String[]{String.valueOf(hikeId)});
        Log.d("DEBUG", "Num rows updated: " + numberOfRowsAffected);

        // Close the database connection
        db.close();

        if(numberOfRowsAffected > 0){
            Toast.makeText(context,"Update successfully", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"Update failed", Toast.LENGTH_SHORT).show();
        }

        return numberOfRowsAffected > 0;
    }

    public boolean deleteHike(int hikeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_HIKE_NAME, COLUMN_HIKE_ID + "=?", new String[]{String.valueOf(hikeId)}) > 0;
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if(bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } else {

            Log.e("getBytesFromBitmap", "Bitmap is null");
            return null;
        }
    }


    public boolean addObservation(Integer idHike, String observation_name, String observation_date, String observation_time, String observation_notes, Bitmap img_observation) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIKE_ID, idHike);
        values.put(COLUMN_OBSERVATION_NAME, observation_name);
        values.put(COLUMN_OBSERVATION_DATE, observation_date);
        values.put(COLUMN_OBSERVATION_TIME, observation_time);
        values.put(COLUMN_OBSERVATION_NOTES, observation_notes);


        // Convert Bitmap to byte array for storage
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        img_observation.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        values.put(COLUMN_OBSERVATION_IMAGE, imageBytes);

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_OBSERVATIONS, null, values);
        db.close();

        if (result == -1) {
            Log.e("DB_ERROR", "Sorry! Having some problems when adding Observation");
            return false;
        } else {
            return true;
        }
    }

    public List<ObservationData> getObservationsByHikeId(int hikeId) {
        List<ObservationData> observations = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_OBSERVATION_ID,
                COLUMN_HIKE_ID,
                COLUMN_OBSERVATION_NAME,
                COLUMN_OBSERVATION_DATE,
                COLUMN_OBSERVATION_TIME,
                COLUMN_OBSERVATION_NOTES,
                COLUMN_OBSERVATION_IMAGE
        };

        String selection = COLUMN_HIKE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(hikeId)};

        Cursor cursor = db.query(
                TABLE_OBSERVATIONS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int observationId = cursor.getInt(cursor.getColumnIndex(COLUMN_OBSERVATION_ID));
                @SuppressLint("Range") String observationName = cursor.getString(cursor.getColumnIndex(COLUMN_OBSERVATION_NAME));
                @SuppressLint("Range") String observationDate = cursor.getString(cursor.getColumnIndex(COLUMN_OBSERVATION_DATE));
                @SuppressLint("Range") String observationTime = cursor.getString(cursor.getColumnIndex(COLUMN_OBSERVATION_TIME));
                @SuppressLint("Range")
                String observationNotes = cursor.getString(cursor.getColumnIndex(COLUMN_OBSERVATION_NOTES));
                List<Pair<String, String>> titlesAndDescriptions = convertStringToList(observationNotes);
                @SuppressLint("Range") byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_OBSERVATION_IMAGE));
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                ObservationData observationData = new ObservationData(observationId, hikeId, observationName, observationDate, observationTime, titlesAndDescriptions, imageBitmap);
                observations.add(observationData);
                Log.d("Observation", "Number of observations retrieved: " + observations.size());
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return observations;

    }

    @SuppressLint("Range")
    public ObservationData getObservationDataById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ObservationData observationData = null;

        String selectQuery = "SELECT * FROM " + TABLE_OBSERVATIONS + " WHERE " + COLUMN_OBSERVATION_ID + " = ?";

        // Using a PreparedStatement to prevent SQL Injection
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

        // Check if data exists and move the cursor to the first row
        if (cursor.moveToFirst()) {
            observationData = new ObservationData();
            observationData.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_OBSERVATION_ID)));
            observationData.setName(cursor.getString(cursor.getColumnIndex(COLUMN_OBSERVATION_NAME)));
            observationData.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_OBSERVATION_DATE)));
            observationData.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_OBSERVATION_TIME)));
            String titlesAndDescriptionsStr = cursor.getString(cursor.getColumnIndex(COLUMN_OBSERVATION_NOTES));
            List<Pair<String, String>> titlesAndDescriptionsList = convertStringToList(titlesAndDescriptionsStr);
            observationData.setTitlesAndDescriptions(titlesAndDescriptionsList);
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_OBSERVATION_IMAGE));

            if (imageBytes != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                observationData.setImage(imageBitmap);
            }

        }

        cursor.close();
        db.close();

        return observationData;
    }

    public boolean updateObservation(int ObservationId, ObservationData observationData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_OBSERVATION_NAME, observationData.getName());
        values.put(COLUMN_OBSERVATION_DATE, observationData.getDate());
        values.put(COLUMN_OBSERVATION_TIME, observationData.getTime());

        // Convert the List<Pair<String, String>> to a String format for saving
        values.put(COLUMN_OBSERVATION_NOTES, observationData.getNotes());
        values.put(COLUMN_OBSERVATION_IMAGE, getBytesFromBitmap(observationData.getImageBitmap()));

        int result = db.update(TABLE_OBSERVATIONS, values, COLUMN_OBSERVATION_ID + " = ? AND " + COLUMN_OBSERVATION_ID + " = ?", new String[]{String.valueOf(ObservationId), String.valueOf(observationData.getId())});
        db.close();
        return result != 0;
    }

    public boolean deleteObservation(int observationId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the observation where ID matches
        int result = db.delete(TABLE_OBSERVATIONS, COLUMN_OBSERVATION_ID + " = ?", new String[]{String.valueOf(observationId)});

        db.close();

        return result != 0;
    }

    private List<Pair<String, String>> convertStringToList(String combinedNotes) {
        List<Pair<String, String>> result = new ArrayList<>();

        String[] separateNotes = combinedNotes.split("\n");

        for (String note : separateNotes) {
            String[] parts = note.split(": ", 2);
            if (parts.length == 2) {
                result.add(new Pair<>(parts[0], parts[1]));
            }
        }

        return result;
    }



    @SuppressLint("Range")
    public String getFullName(String username) {
        String fullName = "defaultName";
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d("DB_DEBUG", "Trying to get fullName for username: " + username);

        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_FULL_NAME},
                COLUMN_USER_NAME + " = ?",
                new String[]{username},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            fullName = cursor.getString(cursor.getColumnIndex(COLUMN_FULL_NAME));
            Log.d("DB_DEBUG", "Retrieved fullName: " + fullName);
            cursor.close();
        } else {
            Log.d("DB_DEBUG", "Cursor is null or could not move to first. Default name will be used.");
        }
        db.close();
        return fullName;
    }



    public boolean addExplore(String explore_name, String explore_location, String explore_date, Bitmap img_explore) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPLORE_NAME, explore_name);
        values.put(COLUMN_EXPLORE_LOCATION, explore_location);
        values.put(COLUMN_EXPLORE_DATE, explore_date);



        // Convert Bitmap to byte array for storage
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        img_explore.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        values.put(COLUMN_EXPLORE_IMAGE, imageBytes);

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_EXPLORE, null, values);
        db.close();

        if (result == -1) {
            Log.e("DB_ERROR", "Sorry! Something went wrong when adding explore");
            return false;
        } else {
            return true;
        }
    }

    public List<ExploreData> getAllExploreData() {
        List<ExploreData> exploreDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        if (db == null || !db.isOpen()) {
            Log.e("DB_ERROR", "Database is not open");
            return null;
        }

        String[] columns = {
                COLUMN_EXPLORE_ID,
                COLUMN_EXPLORE_NAME,
                COLUMN_EXPLORE_LOCATION,
                COLUMN_EXPLORE_DATE,
                COLUMN_EXPLORE_IMAGE
        };

        Cursor cursor = db.query(
                TABLE_EXPLORE,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int explore_id = cursor.getInt(cursor.getColumnIndex(COLUMN_EXPLORE_ID));
                @SuppressLint("Range") String explore_name = cursor.getString(cursor.getColumnIndex(COLUMN_EXPLORE_NAME));
                @SuppressLint("Range") String explore_location = cursor.getString(cursor.getColumnIndex(COLUMN_EXPLORE_LOCATION));
                @SuppressLint("Range") String explore_date = cursor.getString(cursor.getColumnIndex(COLUMN_EXPLORE_DATE));
                @SuppressLint("Range") byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_EXPLORE_IMAGE));

                Bitmap img_explore = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                ExploreData exploreData = new ExploreData(explore_id, explore_name, explore_location, explore_date, img_explore);

                exploreDataList.add(exploreData);
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Log.e("DB_ERROR", "Cursor is null and no data found");
            return null;
        }

        db.close();

        return exploreDataList;
    }



    @SuppressLint("Range")
    public ExploreData getExploreDataById(int exploreId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ExploreData exploreData = null;

        Cursor cursor = db.query(
                TABLE_EXPLORE,
                new String[]{
                        COLUMN_EXPLORE_ID,
                        COLUMN_EXPLORE_NAME,
                        COLUMN_EXPLORE_LOCATION,
                        COLUMN_EXPLORE_DATE,
                        COLUMN_EXPLORE_IMAGE
                }, // The columns to return
                COLUMN_EXPLORE_ID + "=?",
                new String[]{String.valueOf(exploreId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_EXPLORE_IMAGE));
            Bitmap img_explore = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            exploreData = new ExploreData(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_EXPLORE_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EXPLORE_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EXPLORE_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EXPLORE_DATE)),
                    img_explore
            );
            cursor.close();
        }

        db.close();
        return exploreData;
    }



    public void updateExplore(int exploreId, ExploreData updatedExplore) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EXPLORE_NAME, updatedExplore.getNameHike());
        contentValues.put(COLUMN_EXPLORE_LOCATION, updatedExplore.getLocationHike());
        contentValues.put(COLUMN_EXPLORE_DATE, updatedExplore.getDatePost());

        // Assuming you have a method to convert the Bitmap to byte[]
        contentValues.put(COLUMN_EXPLORE_IMAGE, bitmapToByte(updatedExplore.getImageBitmap()));

        // Updating row
        db.update(TABLE_EXPLORE, contentValues, COLUMN_EXPLORE_ID + " = ?",
                new String[]{String.valueOf(exploreId)});

        db.close();

    }

    public byte[] bitmapToByte(Bitmap bitmap) {
        if(bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } else {

            Log.e("getBytesFromBitmap", "Bitmap is null");
            return null;
        }
    }


    public int deleteExplore(int exploreId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the explore matching the given ID.
        int rowsDeleted = db.delete(TABLE_EXPLORE,
                COLUMN_EXPLORE_ID + " = ?",
                new String[]{String.valueOf(exploreId)});

        db.close();
        return rowsDeleted;
    }


    public void bookmarkHike(HikeData hikeData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID_BOOKMARKED, hikeData.getId());
        contentValues.put(COLUMN_NAME_BOOKMARKED, hikeData.getName());
        contentValues.put(COLUMN_LOCATION_BOOKMARKED, hikeData.getLocation());
        contentValues.put(COLUMN_DATE_BOOKMARKED, hikeData.getDate());
        contentValues.put(COLUMN_PARKING_BOOKMARKED, hikeData.getParking());
        contentValues.put(COLUMN_DIFFICULTY_BOOKMARKED, hikeData.getDifficulty());
        contentValues.put(COLUMN_LENGTH_BOOKMARKED, hikeData.getLength());
        contentValues.put(COLUMN_DESCRIPTION_BOOKMARKED, hikeData.getDescription());
        contentValues.put(COLUMN_IMAGE_BOOKMARKED, getBytesFromBitmap(hikeData.getImageBitmap()));

        db.insert(TABLE_BOOKMARKED_HIKE, null, contentValues);

        db.close();
    }



    public List<HikeData> getBookmarkedHikes() {
        List<HikeData> bookmarkedHikes = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Define the columns you want to retrieve
        String[] columns = {
                COLUMN_ID_BOOKMARKED, COLUMN_NAME_BOOKMARKED, COLUMN_LOCATION_BOOKMARKED, COLUMN_DIFFICULTY_BOOKMARKED,
                COLUMN_LENGTH_BOOKMARKED, COLUMN_PARKING_BOOKMARKED, COLUMN_DATE_BOOKMARKED, COLUMN_DESCRIPTION_BOOKMARKED, COLUMN_IMAGE_BOOKMARKED
        };
        Cursor cursor = db.query(TABLE_BOOKMARKED_HIKE, columns, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_BOOKMARKED));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BOOKMARKED));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION_BOOKMARKED));
                @SuppressLint("Range") String difficulty = cursor.getString(cursor.getColumnIndex(COLUMN_DIFFICULTY_BOOKMARKED));
                @SuppressLint("Range") String length = cursor.getString(cursor.getColumnIndex(COLUMN_LENGTH_BOOKMARKED));
                @SuppressLint("Range") String parking = cursor.getString(cursor.getColumnIndex(COLUMN_PARKING_BOOKMARKED));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_BOOKMARKED));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION_BOOKMARKED));
                @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE_BOOKMARKED));

                Bitmap imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

                HikeData hike = new HikeData(id, name, location, date, parking,length, difficulty, description, imageBitmap);
                bookmarkedHikes.add(hike);

            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();

        return bookmarkedHikes;
    }



    public void removeHiking(HikeData hikeData) {
        // Get a writable SQLite database
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKMARKED_HIKE, COLUMN_ID_BOOKMARKED + " = ?", new String[]{String.valueOf(hikeData.getId())});
        db.close();
    }



    public boolean isBookmarked(HikeData hikeData) {
        boolean isBookmarked = false;

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_BOOKMARKED_HIKE + " WHERE " + COLUMN_ID_BOOKMARKED + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(hikeData.getId())});

        if (cursor.moveToFirst()) {
            isBookmarked = true;
        }

        cursor.close();
        db.close();

        return isBookmarked;
    }

    public boolean isHikeBookmarked(int hikeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOKMARKED_HIKE + " WHERE " + COLUMN_ID_BOOKMARKED + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(hikeId)});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }


}
