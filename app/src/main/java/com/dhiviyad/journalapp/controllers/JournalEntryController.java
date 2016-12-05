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
}
