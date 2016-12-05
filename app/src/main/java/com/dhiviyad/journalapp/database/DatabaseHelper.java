package com.dhiviyad.journalapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dhiviyad.journalapp.models.StepsCountData;
import com.dhiviyad.journalapp.utils.DateUtils;

/**
 * Created by dhiviyad on 12/3/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "JournalAppDB";
    public static final int DATABASE_VERSION = 3;

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
}
