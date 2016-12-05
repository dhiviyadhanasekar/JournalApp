package com.dhiviyad.journalapp.models;


import com.dhiviyad.journalapp.utils.DateUtils;

import java.util.Date;

/**
 * Created by dhiviyad on 12/4/16.
 */

public class StepsCountData {

    private long stepsCount;
    private String date;
    private Long id = null;

    public StepsCountData() {
        stepsCount = 0;
        date = DateUtils.getCurrentDate();
    }

    public String getDate() {
        return date;
    }

    public long getStepsCount() {
        return stepsCount;
    }

    public Long getId() {
        return id;
    }

    public void setStepsCount(long stepsCount) {
        this.stepsCount = stepsCount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void incrementStepCount(){
        stepsCount++;
    }
}
