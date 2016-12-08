package com.dhiviyad.journalapp.utils;

import android.util.Log;

import com.dhiviyad.journalapp.constants.AppConstants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static final String getDateFromCalendarViewDate(int day, int month, int year){
        String d = year + "-";
        d += ((int)month/10) > 0 ? month : "0" + month;
        d += "-";
        d += ((int)day/10) > 0 ? day : "0" + day;
        return d;
    }

    public static final long getCalendarDateFromDateFormat(String dateFormat){ //2016-11-16
        long milliTime = 0;
        try {

            Date date = new SimpleDateFormat(AppConstants.DATE_FORMAT).parse(dateFormat);
            milliTime = date.getTime();
            Log.v(null, "DATE:::" + date);
        } catch (ParseException e) {
            e.printStackTrace();
            milliTime = System.currentTimeMillis();
        }
        return milliTime;
    }

    public static final String getEntriesHeaderDateFromDateFormat(String dateFormat){
        String ret = null;
        try {

            Date date = new SimpleDateFormat(AppConstants.DATE_FORMAT).parse(dateFormat);
            DateFormat newdateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            ret = newdateFormat.format(date);
            Log.v(null, "NEWWW DATE:::" + ret);
        } catch (ParseException e) {
            e.printStackTrace();
            ret = dateFormat;
        }
        return ret;
    }

    public static final String getCurrentTime(){
        DateFormat dateFormat = new SimpleDateFormat(AppConstants.TIME_FORMAT);
        Date cal = new Date();
        String d = dateFormat.format(cal);
        System.out.println(d); //2016-11-16
        return d;
    }
}
