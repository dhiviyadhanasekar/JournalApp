package com.dhiviyad.journalapp.constants;

/**
 * Created by dhiviyad on 12/4/16.
 */

public class AppData {

    private String dateSelected;
    private static AppData instance = null;

    public static AppData getInstance(){
        if(instance == null){
            instance = new AppData();
        }
        return instance;
    }

    public void setDateSelected(String dateSelected) {
        this.dateSelected = dateSelected;
    }

    public String getDateSelected() {
        return dateSelected;
    }
}
