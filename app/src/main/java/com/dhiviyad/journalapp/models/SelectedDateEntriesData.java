package com.dhiviyad.journalapp.models;

import java.util.ArrayList;

/**
 * Created by dhiviyad on 12/3/16.
 */

public class SelectedDateEntriesData {

    private ArrayList<JournalEntryData> entries;
    private int stepsCount;

    public SelectedDateEntriesData() {
        entries = new ArrayList<>();
        stepsCount = 0;
    }

    public void setEntries(ArrayList<JournalEntryData> entries) {
        this.entries = entries;
    }

    public void setStepsCount(int stepsCount) {
        this.stepsCount = stepsCount;
    }

    public ArrayList<JournalEntryData> getEntries() {
        return entries;
    }

    public int getStepsCount() {
        return stepsCount;
    }
}
