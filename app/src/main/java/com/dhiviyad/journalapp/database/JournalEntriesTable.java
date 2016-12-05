package com.dhiviyad.journalapp.database;

import android.provider.BaseColumns;

import com.dhiviyad.journalapp.models.JournalEntryData;

/**
 * Created by dhiviyad on 12/3/16.
 */

public final class JournalEntriesTable {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private JournalEntriesTable() {}

    public static class JournalEntryColumns implements BaseColumns {
        public static final String TABLE_NAME = "journal_entries";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TIMESTAMP ="timestamp";
        public static final String COLUMN_COUNTRY_NAME = "country_name";
        public static final String COLUMN_STATE_NAME = "state_name";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_WEATHER = "weather";
        public static final String COLUMN_IMAGE_TYPE = "image_type";
        public static final String COLUMN_IMAGE_URI = "image_url";
    }

    public static final String SQL_CREATE_ENTRY = "CREATE TABLE IF NOT EXISTS " + JournalEntryColumns.TABLE_NAME + " (" +
            JournalEntryColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            JournalEntryColumns.COLUMN_DESCRIPTION + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_TIMESTAMP +  DatabaseFieldTypes.DATE_TIME //+ "DEFAULT CURRENT_DATE"
            + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_COUNTRY_NAME + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_CITY_NAME + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_STATE_NAME + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_WEATHER + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_IMAGE_TYPE + DatabaseFieldTypes.INTEGER_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_IMAGE_URI + DatabaseFieldTypes.TEXT_TYPE + " )";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + JournalEntryColumns.TABLE_NAME;;
}
