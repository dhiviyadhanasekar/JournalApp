package com.dhiviyad.journalapp.controllers;

import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dhiviyad on 12/8/16.
 */

public class FileOperationsController {
    private static final String FILENAME = "journal_settings.txt";
    private Context context;

    public FileOperationsController(Context context) {
        this.context = context;
    }

    public void writeSettingsToFile(String string) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Unable to save, try again!", Toast.LENGTH_LONG).show();
        }
    }

    public String readSettingsFromFile() {

        String temp="";
        try {
            FileInputStream fin = context.openFileInput(FILENAME);
            int c;
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }
}
