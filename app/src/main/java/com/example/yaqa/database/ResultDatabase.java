package com.example.yaqa.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.yaqa.model.QuestionSet;
import com.example.yaqa.model.Result;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ResultDatabase {
    private static AppDbHelper dbHelper = null;
    private static SQLiteDatabase dbRead = null;
    private static SQLiteDatabase dbWrite = null;

    public static boolean addNewResult(Result entry) {
        if (dbHelper == null) {
            System.out.println("No DB context");
            return false;
        }
        dbWrite = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put(AppDataContract.ResultEntry.COLUMN_NAME_UUID, entry.UUID);
        values.put(AppDataContract.ResultEntry.COLUMN_NAME_PLAYTIME, sdf.format(entry.playTime));
        values.put(AppDataContract.ResultEntry.COLUMN_NAME_SCORE, entry.score);
        values.put(AppDataContract.ResultEntry.COLUMN_NAME_QUESTION_COUNT, entry.total_count);
        values.put(AppDataContract.ResultEntry.COLUMN_NAME_CORRECT_COUNT, entry.correct_count);
        long newRowId = dbWrite.insert(AppDataContract.ResultEntry.TABLE_NAME, null, values);
        return true;
    }

    public static int getResultCount() {
        int result = 0;
        if (dbHelper == null) {
            System.out.println("No DB context");
            return result;
        }
        dbRead = dbHelper.getReadableDatabase();
        String sql = "SELECT COUNT(" + AppDataContract.ResultEntry.COLUMN_NAME_UUID + ") FROM " + AppDataContract.ResultEntry.TABLE_NAME;
        Cursor cursor = dbRead.rawQuery(sql, null);
        if(cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    public static int getResultHighest() {
        int result = 0;
        if (dbHelper == null) {
            System.out.println("No DB context");
            return result;
        }
        dbRead = dbHelper.getReadableDatabase();
        String sql = "SELECT MAX(" + AppDataContract.ResultEntry.COLUMN_NAME_SCORE + ") FROM " + AppDataContract.ResultEntry.TABLE_NAME;
        Cursor cursor = dbRead.rawQuery(sql, null);
        if(cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    public static int getTotalCorrect() {
        int result = 0;
        if (dbHelper == null) {
            System.out.println("No DB context");
            return result;
        }
        dbRead = dbHelper.getReadableDatabase();
        String sql = "SELECT SUM(" + AppDataContract.ResultEntry.COLUMN_NAME_CORRECT_COUNT + ") FROM " + AppDataContract.ResultEntry.TABLE_NAME;
        Cursor cursor = dbRead.rawQuery(sql, null);
        if(cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    public static Result getMostRecentResult() {
        Result result = null;
        if (dbHelper == null) {
            System.out.println("No DB context");
            return result;
        }
        dbRead = dbHelper.getReadableDatabase();
        String[] projection = {
                AppDataContract.ResultEntry.COLUMN_NAME_UUID,
                AppDataContract.ResultEntry.COLUMN_NAME_PLAYTIME,
                AppDataContract.ResultEntry.COLUMN_NAME_SCORE,
                AppDataContract.ResultEntry.COLUMN_NAME_CORRECT_COUNT,
                AppDataContract.ResultEntry.COLUMN_NAME_QUESTION_COUNT
        };

        String sortOrder = AppDataContract.ResultEntry.COLUMN_NAME_PLAYTIME + " DESC";
        // How you want the results sorted in the resulting Cursor

        Cursor cursor = dbRead.query(
                AppDataContract.ResultEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder           // The sort order
        );


        if (cursor.moveToNext()) {
            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date date = new Date();
            try {
                date = simpleDateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.ResultEntry.COLUMN_NAME_PLAYTIME)));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println(cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.ResultEntry.COLUMN_NAME_PLAYTIME)));
            result = new Result(
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDataContract.ResultEntry.COLUMN_NAME_UUID)),
                    date,
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDataContract.ResultEntry.COLUMN_NAME_SCORE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDataContract.ResultEntry.COLUMN_NAME_CORRECT_COUNT)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDataContract.ResultEntry.COLUMN_NAME_QUESTION_COUNT))
            );
        }
        cursor.close();
        return result;
    }

    public static boolean wipeAllResult() {
        if (dbHelper == null) {
            System.out.println("No DB context");
            return false;
        }
        dbWrite = QuestionSetMetadataDatabase.getDbHelper().getWritableDatabase();

        int count = dbWrite.delete(
                AppDataContract.ResultEntry.TABLE_NAME,
                null,
                null);

        return true;
    }

    public static void setDbHelper(AppDbHelper dbHelper) {
        ResultDatabase.dbHelper = dbHelper;
    }

    public static AppDbHelper getDbHelper() {
        return dbHelper;
    }
}
