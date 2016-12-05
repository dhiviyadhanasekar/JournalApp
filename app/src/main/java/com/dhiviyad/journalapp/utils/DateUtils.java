package com.dhiviyad.journalapp.utils;

import com.dhiviyad.journalapp.constants.AppConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dhiviyad on 12/4/16.
 */

public class DateUtils {
    public static final String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat(AppConstants.DATE_FORMAT);
        Date cal = new Date();
        String d = dateFormat.format(cal);
        System.out.println(d); //2016-11-16
        return d;
    }
}
