package com.dhiviyad.journalapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dhiviyad.journalapp.models.JournalEntryData;
import com.dhiviyad.journalapp.models.StepsCountData;
import com.dhiviyad.journalapp.utils.DateUtils;

/**
 * Created by dhiviyad on 12/3/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "JournalAppDB";
    public static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(DATABASE_NAME, "in create");
        db.execSQL(JournalEntriesTable.SQL_CREATE_ENTRY);
        db.execSQL(StepsCountTable.SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL(JournalEntriesTable.SQL_DELETE);
        db.execSQL(StepsCountTable.SQL_DELETE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public StepsCountData createFetchStepsCount(){
        String todaysDate = DateUtils.getCurrentDate();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(StepsCountTable.SQL_SELECT + " WHERE "
                + StepsCountTable.StepsCountColumns.COLUMN_DATE + "='" + todaysDate + "'" , null);
        StepsCountData u = new StepsCountData();
        if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            u.setId( cursor.getLong( cursor.getColumnIndex( StepsCountTable.StepsCountColumns.COLUMN_ID )));
            u.setDate( cursor.getString(cursor.getColumnIndex( StepsCountTable.StepsCountColumns.COLUMN_DATE ))) ;
            u.setStepsCount( cursor.getLong( cursor.getColumnIndex(StepsCountTable.StepsCountColumns.COLUMN_COUNT )));
        } else {
            long newRowId = insertStepsCountRow(u);
            u.setId(newRowId);
            Log.i("User table created => ", "stepsCountid is" + newRowId );
        }
        cursor.close();
        return u;
    }

    public long insertStepsCountRow(StepsCountData u) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getStepsCountContentValues(u);
        return db.insert(StepsCountTable.StepsCountColumns.TABLE_NAME, null, values);
    }

    @NonNull
    private ContentValues getStepsCountContentValues(StepsCountData u) {
        ContentValues values = new ContentValues();
        values.put(StepsCountTable.StepsCountColumns.COLUMN_DATE,u.getDate());
        values.put(StepsCountTable.StepsCountColumns.COLUMN_COUNT, u.getStepsCount());
        return values;
    }

    public void updateStepsCountRow(StepsCountData details) {

        SQLiteDatabase db = this.getReadableDatabase();

        // New value for each column
        ContentValues values = getStepsCountContentValues(details);

        // Which row to update, based on the title
        String selection = StepsCountTable.StepsCountColumns.COLUMN_ID + " = ?";
        String[] selectionArgs = { details.getId()+"" };

        int count = db.update(
                StepsCountTable.StepsCountColumns.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        Log.i("User table updated", "count =" + count);
    }


    public int updateEntry(JournalEntryData journalEntry) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = getEntryContentValues(journalEntry);

        // Which row to update, based on the title
        String selection = JournalEntriesTable.JournalEntryColumns.COLUMN_ID + " = ?";
        String[] selectionArgs = { journalEntry.getId()+"" };

        int count = db.update(
                JournalEntriesTable.JournalEntryColumns.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        Log.i("User table updated", "count =" + count);
        return count;
    }

    public long insertEntry(JournalEntryData journalEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getEntryContentValues(journalEntry);
        return db.insert(JournalEntriesTable.JournalEntryColumns.TABLE_NAME, null, values);
    }

    private ContentValues getEntryContentValues(JournalEntryData u) {
        ContentValues values = new ContentValues();
        if(u.getLatitude() != null) values.put(JournalEntriesTable.JournalEntryColumns.COLUMN_LATITUDE, u.getLatitude());
        if(u.getLongitude() != null) values.put(JournalEntriesTable.JournalEntryColumns.COLUMN_LONGITUDE, u.getLongitude());
        if(u.getCountryName() != null) values.put(JournalEntriesTable.JournalEntryColumns.COLUMN_COUNTRY_NAME, u.getCountryName());
        if(u.getStateName() != null) values.put(JournalEntriesTable.JournalEntryColumns.COLUMN_STATE_NAME, u.getStateName());
        if(u.getCityName() != null) values.put(JournalEntriesTable.JournalEntryColumns.COLUMN_CITY_NAME, u.getCityName());
        if(u.getWeather() != null) values.put(JournalEntriesTable.JournalEntryColumns.COLUMN_WEATHER, u.getWeather());
        if(u.getPicture() != null) values.put(JournalEntriesTable.JournalEntryColumns.COLUMN_PICTURE, u.getPicture());
        if(u.getTime() != null) values.put(JournalEntriesTable.JournalEntryColumns.COLUMN_TIME, u.getTime());
        if(u.getDate() != null) values.put(JournalEntriesTable.JournalEntryColumns.COLUMN_DATE, u.getDate());
        values.put(JournalEntriesTable.JournalEntryColumns.COLUMN_TIMESTAMP, u.getTimestamp());
        if(u.getDescription() != null )values.put(JournalEntriesTable.JournalEntryColumns.COLUMN_DESCRIPTION, u.getDescription());
        return values;
    }
}
