package com.dhiviyad.journalapp.controllers;

import android.content.Context;
import android.graphics.Color;

import com.dhiviyad.journalapp.database.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by dhiviyad on 12/8/16.
 */

public class LocationListController {
    Context c;
    DatabaseHelper db;

    public LocationListController(Context c) {
        this.c = c;
        db = new DatabaseHelper(c);
    }

    public ArrayList<String> getCountriesVisited(){
        return db.getCountriesVisited();
    }

    public int getColorForRowPos(int pos){
        if(pos %2 == 1) {
            // Set a background color for ListView regular row/item
            return Color.parseColor("#E0E0E0");

        } else {
            // Set the background color for alternate row/item
            return Color.parseColor("#FFFFFF");
        }
    }

    public ArrayList<String> getStatesVisited(String countryName){
        return db.getStatesVisited(countryName);
    }
}
