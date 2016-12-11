package com.dhiviyad.journalapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.dhiviyad.journalapp.database.DatabaseHelper;
import com.dhiviyad.journalapp.models.JournalEntryData;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.dhiviyad.journalapp", appContext.getPackageName());
    }

    //functional test case for the main database action
    //as long as this works, the app should work fine
    @Test
    public void savingJournalEntryTest() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseHelper db = new DatabaseHelper(appContext);
        JournalEntryData j = new JournalEntryData();
        j.setCountryName("India");
        j.setStateName("Tamil Nadu");
        j.setCityName("Chennai");
        j.setDescription("Went to one of my favorite places on earth - Marina Beach");
        Long id = db.insertEntry(j);
        assertTrue(id != null);
    }

}
