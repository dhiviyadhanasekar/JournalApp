package com.dhiviyad.journalapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dhiviyad on 12/3/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "JournalAppDB";
    public static final int DATABASE_VERSION = 1;

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
}
