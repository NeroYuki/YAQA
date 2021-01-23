package com.example.yaqa.database;

import android.provider.BaseColumns;

public final class AppDataContract {
    private AppDataContract() {}

    public static class ResultEntry implements BaseColumns {
        public static final String TABLE_NAME = "tbResult";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_PLAYTIME = "playtime";
        public static final String COLUMN_NAME_SCORE = "score";
        public static final String COLUMN_NAME_QUESTION_COUNT = "question_count";
        public static final String COLUMN_NAME_CORRECT_COUNT = "correct_count";
    }

    public static class QuestionSetMetadataEntry implements BaseColumns {
        public static final String TABLE_NAME = "tbSetMetaData";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COUNT = "question_count";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_FILEPATH = "file_path";
    }
}
