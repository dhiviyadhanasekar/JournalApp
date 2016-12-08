package com.dhiviyad.journalapp.controllers;

import android.content.Context;

import com.dhiviyad.journalapp.database.DatabaseHelper;
import com.dhiviyad.journalapp.models.JournalEntryData;

/**
 * Created by dhiviyad on 12/3/16.
 */

public class JournalEntryController {

    DatabaseHelper db;
    private JournalEntryData journalEntry;
    private JournalEntryController journalEntryController;

    public JournalEntryController(Context c) {
        db = new DatabaseHelper(c);
    }


    public void createExistingEntry(JournalEntryData jdata){
        journalEntry = jdata;
    }
    public void createNewEntry() {
        journalEntry = new JournalEntryData();
    }

    public void updateLocCoordinates(double latitude, double longitude) {
        journalEntry.setLatitude(latitude);
        journalEntry.setLongitude(longitude);
    }

    public void updateAddress(String cityName, String stateName, String countryName) {
        journalEntry.setCityName(cityName);
        journalEntry.setStateName(stateName);
        journalEntry.setCountryName(countryName);
    }

    public JournalEntryData getEntry() {
        return journalEntry;
    }

    public void updateWeather(String temp) {
        journalEntry.setWeather(temp);
    }

    public JournalEntryData saveEntryToDB() {
//        journalEntry.setDescription(description);
        if(journalEntry.getId() == null){
            long id = db.insertEntry(journalEntry);
            journalEntry.setId( id );
        } else {
            db.updateEntry(journalEntry);
        }
        return journalEntry;
    }

    public void saveDescription(String text) {
        journalEntry.setDescription(text);
    }
}
