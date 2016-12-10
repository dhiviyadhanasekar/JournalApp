package com.dhiviyad.journalapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dhiviyad.journalapp.models.JournalEntryData;
import com.dhiviyad.journalapp.models.SelectedDateEntriesData;
import com.dhiviyad.journalapp.models.StepsCountData;
import com.dhiviyad.journalapp.utils.DateUtils;

import java.util.ArrayList;

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
        StepsCountData u = getStepsCountData(todaysDate);
        if(u.getId() == null) {
            long newRowId = insertStepsCountRow(u);
            u.setId(newRowId);
            Log.i("User table created => ", "stepsCountid is" + newRowId );
        }
        return u;
    }

    @NonNull
    public StepsCountData getStepsCountData(String todaysDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(StepsCountTable.SQL_SELECT + " WHERE "
                + StepsCountTable.StepsCountColumns.COLUMN_DATE + "='" + todaysDate + "'" , null);
        StepsCountData u = new StepsCountData();
        if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            u.setId( cursor.getLong( cursor.getColumnIndex( StepsCountTable.StepsCountColumns.COLUMN_ID )));
            u.setDate( cursor.getString(cursor.getColumnIndex( StepsCountTable.StepsCountColumns.COLUMN_DATE ))) ;
            u.setStepsCount( cursor.getLong( cursor.getColumnIndex(StepsCountTable.StepsCountColumns.COLUMN_COUNT )));
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

    public ArrayList<JournalEntryData> fetchEntriesForDate(String date) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(JournalEntriesTable.SQL_SELECT_BY_DATE(date), null);
        ArrayList<JournalEntryData> entriesArr = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                JournalEntryData entry = new JournalEntryData();
                entry.setDescription(cursor.getString( cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_DESCRIPTION) ));
                entry.setWeather(cursor.getString( cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_WEATHER ) ));
                entry.setCountryName( cursor.getString( cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_COUNTRY_NAME)));
                entry.setStateName( cursor.getString(cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_STATE_NAME)));
                entry.setCityName( cursor.getString( cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_CITY_NAME)));
                entry.setDate( cursor.getString(cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_DATE)) );
                entry.setId( cursor.getLong(cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_ID)));
                entry.setLatitude( cursor.getDouble( cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_LATITUDE)));
                entry.setLongitude( cursor.getDouble( cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_LONGITUDE )));
                entry.setPicture( cursor.getString( cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_PICTURE)));
                entry.setTime( cursor.getString( cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_TIME)));
                entry.setTimestamp( cursor.getLong( cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_TIMESTAMP)));
                Log.v("DB","Entryyyy::::" + entry.getId() );
                entriesArr.add(entry);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return entriesArr;
    }

    public int deleteJournalEntry(Long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String table = JournalEntriesTable.JournalEntryColumns.TABLE_NAME;
        String whereClause = JournalEntriesTable.JournalEntryColumns.COLUMN_ID + " = ?";
        String[] whereArgs = new String[] { id + "" };
        return db.delete(table, whereClause, whereArgs);

    }

    public ArrayList<String> getCountriesVisited(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(JournalEntriesTable.SQL_SELECT_COUNTRIES, null);
        ArrayList<String> countriesArr = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String country = cursor.getString(cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_COUNTRY_NAME));
                countriesArr.add(country);
                Log.v("DB", "country name = " + country);
                cursor.moveToNext();
            }
        }
        return countriesArr;
    }

    public ArrayList<String> getStatesVisited(String countryName){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = JournalEntriesTable.SQL_SELECT_STATES;
        if(countryName != null && countryName.length() > 0){
            query += " AND " + JournalEntriesTable.JournalEntryColumns.COLUMN_COUNTRY_NAME
                    + "='" + countryName + "'";
        }
        Cursor cursor = db.rawQuery( query, null);
        ArrayList<String> countriesArr = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String country = cursor.getString(cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_STATE_NAME));
                countriesArr.add(country);
                Log.v("DB", "state name = " + country);
                cursor.moveToNext();
            }
        }
        return countriesArr;

    }

    public ArrayList<String> getCitiesVisited(String countryName) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = JournalEntriesTable.SQL_SELECT_CITIES;
        if(countryName != null && countryName.length() > 0){
            query += " AND " + JournalEntriesTable.JournalEntryColumns.COLUMN_STATE_NAME
                    + "='" + countryName + "'";
        }
        Cursor cursor = db.rawQuery( query, null);
        ArrayList<String> countriesArr = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String country = cursor.getString(cursor.getColumnIndex(JournalEntriesTable.JournalEntryColumns.COLUMN_CITY_NAME));
                countriesArr.add(country);
                Log.v("DB", "city name = " + country);
                cursor.moveToNext();
            }
        }
        return countriesArr;
    }
}
