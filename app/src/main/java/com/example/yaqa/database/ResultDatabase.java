package com.example.yaqa.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.yaqa.model.Result;

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
        values.put(AppDataContract.ResultEntry.COLUMN_NAME_UUID, entry.UUID);
        values.put(AppDataContract.ResultEntry.COLUMN_NAME_PLAYTIME, entry.playTime.toString());
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
