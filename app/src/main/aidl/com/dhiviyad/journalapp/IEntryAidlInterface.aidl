// IEntryAidlInterface.aidl
package com.dhiviyad.journalapp;

// Declare any non-default types here with import statements

interface IEntryAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

     void initNewEntry();
     void sendEntryData();
     void saveEntryData();
     void saveDescription(String text);
}
