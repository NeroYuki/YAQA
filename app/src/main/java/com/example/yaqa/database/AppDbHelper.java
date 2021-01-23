package com.example.yaqa.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AppData.db";

    public AppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SchemaDefinition.SQL_CREATE_TABLE_RESULT);
        db.execSQL(SchemaDefinition.SQL_CREATE_TABLE_QUESTION_METADATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SchemaDefinition.SQL_DELETE_TABLE_RESULT);
        db.execSQL(SchemaDefinition.SQL_DELETE_TABLE_QUESTION_METADATA);
        onCreate(db);
    }
}
