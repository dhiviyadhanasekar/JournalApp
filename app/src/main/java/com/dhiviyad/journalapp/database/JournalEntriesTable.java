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

        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";

        public static final String COLUMN_COUNTRY_NAME = "country_name";
        public static final String COLUMN_STATE_NAME = "state_name";
        public static final String COLUMN_CITY_NAME = "city_name";

        public static final String COLUMN_WEATHER = "weather";
        public static final String COLUMN_PICTURE = "picture";

        public static final String COLUMN_TIME ="entry_time";
        public static final String COLUMN_DATE = "entry_date";
        public static final String COLUMN_DESCRIPTION = "description";

        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final String SQL_CREATE_ENTRY = "CREATE TABLE IF NOT EXISTS " + JournalEntryColumns.TABLE_NAME + " (" +
            JournalEntryColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            JournalEntryColumns.COLUMN_LATITUDE + DatabaseFieldTypes.REAL + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_LONGITUDE + DatabaseFieldTypes.REAL + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_COUNTRY_NAME + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_CITY_NAME + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_STATE_NAME + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_WEATHER + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_PICTURE + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_TIME + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_DATE + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_DESCRIPTION + DatabaseFieldTypes.TEXT_TYPE + DatabaseFieldTypes.COMMA_SEP +
            JournalEntryColumns.COLUMN_TIMESTAMP + DatabaseFieldTypes.INTEGER_TYPE
            + " )";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + JournalEntryColumns.TABLE_NAME;

    public static final String SQL_SELECT_ALL = "SELECT * FROM " + JournalEntryColumns.TABLE_NAME ;
    public static final String SQL_SELECT_BY_DATE(String date){
        return SQL_SELECT_ALL + " WHERE " + JournalEntryColumns.COLUMN_DATE + " = '" + date
                + "' ORDER BY " + JournalEntryColumns.COLUMN_TIMESTAMP;
    }
    public static final String SQL_SELECT_COUNTRIES = "SELECT DISTINCT " + JournalEntryColumns.COLUMN_COUNTRY_NAME + " FROM "
                                            + JournalEntryColumns.TABLE_NAME + " WHERE " + JournalEntryColumns.COLUMN_COUNTRY_NAME
                                            + " IS NOT NULL";

    public static final String SQL_SELECT_STATES = "SELECT DISTINCT " + JournalEntryColumns.COLUMN_STATE_NAME + " FROM "
            + JournalEntryColumns.TABLE_NAME + " WHERE " + JournalEntryColumns.COLUMN_STATE_NAME
            + " IS NOT NULL";

    public static final String SQL_SELECT_CITIES = "SELECT DISTINCT " + JournalEntryColumns.COLUMN_CITY_NAME + " FROM "
            + JournalEntryColumns.TABLE_NAME + " WHERE " + JournalEntryColumns.COLUMN_CITY_NAME
            + " IS NOT NULL";
}

