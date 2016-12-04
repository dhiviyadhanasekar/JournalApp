package com.dhiviyad.journalapp.database;

import android.provider.BaseColumns;

/**
 * Created by dhiviyad on 12/3/16.
 */

public class StepsCountTable {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private StepsCountTable(){}

    public static class StepsCountColumns implements BaseColumns {
        public static final String TABLE_NAME = "steps_count";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_COUNT = "steps_count";
        public static final String COLUMN_ID = "id";
    }

    public static final String SQL_CREATE_ENTRY = "CREATE TABLE IF NOT EXISTS " + StepsCountColumns.TABLE_NAME + " (" +
            StepsCountColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            StepsCountColumns.COLUMN_DATE +  DatabaseFieldTypes.DATE_TIME //+ "DEFAULT CURRENT_DATE"
            + DatabaseFieldTypes.COMMA_SEP +
            StepsCountColumns.COLUMN_COUNT + DatabaseFieldTypes.INTEGER_TYPE + " )";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + StepsCountColumns.TABLE_NAME;;
}
