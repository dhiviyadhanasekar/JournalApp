package com.dhiviyad.journalapp;

import com.dhiviyad.journalapp.utils.DateUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
//    @Test
//    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
//    }

    @Test
    public void getDateFromCalendarViewDateTest() throws Exception {
        String date = DateUtils.getDateFromCalendarViewDate(2, 1, 1989);
        assertEquals(date, "1989-01-02");
    }
}