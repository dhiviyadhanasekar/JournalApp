package com.dhiviyad.journalapp.controllers;

import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;

import com.dhiviyad.journalapp.database.DatabaseHelper;
import com.dhiviyad.journalapp.models.StepsCountData;
import com.dhiviyad.journalapp.utils.DateUtils;

/**
 * Created by dhiviyad on 12/3/16.
 */

public class StepsCountController {

    DatabaseHelper db;
    StepsCountData stepsCountData;

    public StepsCountController(Context c) {
        db = new DatabaseHelper(c);
        stepsCountData = db.createFetchStepsCount();//new StepsCountData();
        System.out.println("stepsCountdata id =>> " + stepsCountData.getStepsCount() + " => id => "
        + stepsCountData.getId() + "   , date =>" + stepsCountData.getDate() );
    }

    public long processStepDetected(){
        if(DateUtils.getCurrentDate().equalsIgnoreCase(stepsCountData.getDate())) {
            stepsCountData.incrementStepCount();
            return stepsCountData.getStepsCount();
        } else {
            this.saveStepCountToDB();
            stepsCountData = new StepsCountData();
            stepsCountData.incrementStepCount();
            return  stepsCountData.getStepsCount();
        }
    }

    public void saveStepCountToDB() {
        if(stepsCountData.getId() == null) {
            db.insertStepsCountRow(stepsCountData);
        } else {
            db.updateStepsCountRow(stepsCountData);
        }
        stepsCountData = db.createFetchStepsCount();//new StepsCountData();
//        return stepsCountData;
    }

    public long getStepsCount() {
        return stepsCountData.getStepsCount();
    }
}
