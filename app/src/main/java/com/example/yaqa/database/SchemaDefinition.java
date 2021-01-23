package com.example.yaqa.database;

public class SchemaDefinition {
    public static final String SQL_CREATE_TABLE_RESULT =
            "CREATE TABLE " + AppDataContract.ResultEntry.TABLE_NAME + " ("
            + AppDataContract.ResultEntry.COLUMN_NAME_UUID + " TEXT PRIMARY KEY, "
            + AppDataContract.ResultEntry.COLUMN_NAME_PLAYTIME + " TEXT, "
            + AppDataContract.ResultEntry.COLUMN_NAME_SCORE + " INTEGER, "
            + AppDataContract.ResultEntry.COLUMN_NAME_QUESTION_COUNT + " INTEGER, "
            + AppDataContract.ResultEntry.COLUMN_NAME_CORRECT_COUNT + " INTEGER);";
    public static final String SQL_DELETE_TABLE_RESULT =
            "DROP TABLE IF EXISTS" + AppDataContract.ResultEntry.TABLE_NAME;

    public static final String SQL_CREATE_TABLE_QUESTION_METADATA =
            "CREATE TABLE " + AppDataContract.QuestionSetMetadataEntry.TABLE_NAME + " ("
                    + AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_UUID + " TEXT PRIMARY KEY, "
                    + AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_TITLE + " TEXT, "
                    + AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_DESCRIPTION + " TEXT, "
                    + AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_COUNT + " INTEGER, "
                    + AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_AUTHOR + " TEXT, "
                    + AppDataContract.QuestionSetMetadataEntry.COLUMN_NAME_FILEPATH + " TEXT);";
    public static final String SQL_DELETE_TABLE_QUESTION_METADATA  =
            "DROP TABLE IF EXISTS" + AppDataContract.QuestionSetMetadataEntry.TABLE_NAME;
}
