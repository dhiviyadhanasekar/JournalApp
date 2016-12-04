package com.dhiviyad.journalapp.controllers;

import android.content.Context;
import android.widget.Toast;

import com.dhiviyad.journalapp.database.DatabaseHelper;
import com.dhiviyad.journalapp.models.StepsCountData;

/**
 * Created by dhiviyad on 12/3/16.
 */

public class StepsCountController {

    DatabaseHelper db;
    StepsCountData stepsCountData;

    public StepsCountController(Context c) {
        db = new DatabaseHelper(c);
        stepsCountData = new StepsCountData();

    }

    public long processStepDetected(){
//        Date currentDate = new Date();
        stepsCountData.incrementStepCount();
        return stepsCountData.getStepsCount();

    }
}
