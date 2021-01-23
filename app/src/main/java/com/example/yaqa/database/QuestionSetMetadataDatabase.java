package com.example.yaqa.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.yaqa.model.Question;
import com.example.yaqa.model.QuestionSet;

import java.util.ArrayList;

public class QuestionSetMetadataDatabase {
    private static AppDbHelper dbHelper = null;
    private static SQLiteDatabase dbRead = null;
    private static SQLiteDatabase dbWrite = null;
    public static ArrayList<QuestionSet> getAllSetMetadata() {
        ArrayList<QuestionSet> result = new ArrayList<>();
        if (dbHelper == null) {
            System.out.println("No DB context");
            return result;
        }
        if (dbRead == null) {
            dbRead = dbHelper.getReadableDatabase();
        }
        String[] projection = {
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_UUID,
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_TITLE,
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_DESCRIPTION,
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_COUNT,
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_AUTHOR,
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_FILEPATH
        };


        // How you want the results sorted in the resulting Cursor

        Cursor cursor = dbRead.query(
                AppDataContract.QuestionSetMetadataEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null           // The sort order
        );


        while(cursor.moveToNext()) {
            QuestionSet entry = new QuestionSet(
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_UUID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_COUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_AUTHOR))
            );
            entry.file_path = cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_FILEPATH));
            result.add(entry);
        }
        cursor.close();
        return result;
    }

    public static void addInitialData() {
        ContentValues values = new ContentValues();
        QuestionSet initialEntry = new QuestionSet("00000000-0000-0000-0000-000000000001","Example Set", "Yeh, example set", 4, "Guest");
        initialEntry.file_path = "/Example_set/";
        addQuestionSet(initialEntry);
    }

    public static boolean updateSelectedQuestionSet(QuestionSet entry) {
        if (dbHelper == null) {
            System.out.println("No DB context");
            return false;
        }
        dbWrite = QuestionSetMetadataDatabase.getDbHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_TITLE, entry.name);
        values.put(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_DESCRIPTION, entry.desc);
        values.put(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_COUNT, entry.number);
        values.put(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_AUTHOR, entry.author);
        String selection = AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_UUID + " = ?";
        String[] selectionArgs = { entry.uuid };

        int count = dbWrite.update(
                AppDataContract.QuestionSetMetadataEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) return true;
        else return false;
    }

    public static boolean addQuestionSet(QuestionSet entry) {
        if (dbHelper == null) {
            System.out.println("No DB context");
            return false;
        }
        dbWrite = QuestionSetMetadataDatabase.getDbHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_TITLE, entry.name);
        values.put(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_DESCRIPTION, entry.desc);
        values.put(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_COUNT, entry.number);
        values.put(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_AUTHOR, entry.author);
        values.put(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_UUID, entry.uuid);
        values.put(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_FILEPATH, entry.file_path);
        long newRowId = dbWrite.insert(AppDataContract.QuestionSetMetadataEntry.TABLE_NAME, null, values);
        return true;
    }

    public static boolean deleteQuestionSet(QuestionSet entry) {
        if (dbHelper == null) {
            System.out.println("No DB context");
            return false;
        }
        dbWrite = QuestionSetMetadataDatabase.getDbHelper().getWritableDatabase();

        String selection = AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_UUID + " = ?";
        String[] selectionArgs = { entry.uuid };

        int count = dbWrite.delete(
                AppDataContract.QuestionSetMetadataEntry.TABLE_NAME,
                selection,
                selectionArgs);

        if (count > 0) return true;
        else return false;
    }

    public static boolean deleteAllSet() {
        if (dbHelper == null) {
            System.out.println("No DB context");
            return false;
        }
        dbWrite = QuestionSetMetadataDatabase.getDbHelper().getWritableDatabase();

        int count = dbWrite.delete(
                AppDataContract.QuestionSetMetadataEntry.TABLE_NAME,
                null,
                null);

        return true;
    }

    public static QuestionSet getQuestionSetByUUID(String uuid) {
        QuestionSet result = new QuestionSet("", "", "", 0, "");
        if (dbHelper == null) {
            System.out.println("No DB context");
            return result;
        }
        if (dbRead == null) {
            dbRead = dbHelper.getReadableDatabase();
        }
        String[] projection = {
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_UUID,
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_TITLE,
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_DESCRIPTION,
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_COUNT,
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_AUTHOR,
                AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_FILEPATH
        };

        String selection = AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_UUID + " = ?";
        String[] selectionArgs = { uuid };


        // How you want the results sorted in the resulting Cursor

        Cursor cursor = dbRead.query(
                AppDataContract.QuestionSetMetadataEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null           // The sort order
        );

        if(cursor.moveToNext()) {
            result = new QuestionSet(
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_UUID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_COUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_AUTHOR))
            );
            result.file_path = cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_FILEPATH));
        }
        cursor.close();
        return result;
    }

    public static void setDbHelper(AppDbHelper dbHelper) {
        QuestionSetMetadataDatabase.dbHelper = dbHelper;
    }

    public static AppDbHelper getDbHelper() {
        return dbHelper;
    }
}
