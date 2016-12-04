package com.dhiviyad.journalapp.models;

import java.util.Date;

/**
 * Created by dhiviyad on 12/4/16.
 */

public class StepsCountData {
    private long stepsCount;
    private Date date;

    public StepsCountData() {
        stepsCount = 0;
        date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public long getStepsCount() {
        return stepsCount;
    }

    public void incrementStepCount(){
        stepsCount++;
    }
}
