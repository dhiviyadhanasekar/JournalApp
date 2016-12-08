package com.dhiviyad.journalapp.controllers;

import android.content.Context;

import com.dhiviyad.journalapp.database.DatabaseHelper;
import com.dhiviyad.journalapp.models.JournalEntryData;
import com.dhiviyad.journalapp.models.SelectedDateEntriesData;

import java.util.ArrayList;

/**
 * Created by dhiviyad on 12/3/16.
 */

public class MainPageController {

    DatabaseHelper db;

    public MainPageController(Context context) {
        db = new DatabaseHelper(context);
    }

    public ArrayList<JournalEntryData> getEntries(String date){
        return db.fetchEntriesForDate(date);
//        SelectedDateEntriesData selectedEntries = new SelectedDateEntriesData();
//        selectedEntries.setEntries(db.fetchEntriesForDate(date));
//        selectedEntries.setStepsCount(db.fetchStepCountForDate(date));
////        selectedEntries.setStepsCount(0);
//        return selectedEntries;
    }

    public long getStepCounts(String date){
        return db.getStepsCountData(date).getStepsCount();
    }

    public int deleteEntry(Long id) {
        return db.deleteJournalEntry(id);
    }
}
