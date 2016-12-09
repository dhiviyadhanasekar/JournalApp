package com.dhiviyad.journalapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dhiviyad.journalapp.controllers.FileOperationsController;

public class SettingsActivity extends AppCompatActivity {

    FileOperationsController fileOperationsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.activity_settings);
        if(fileOperationsController == null) {
            fileOperationsController = new FileOperationsController(getApplicationContext());
        }
        loadSettings();
    }

    public void loadSettings(){
        String name = fileOperationsController.readSettingsFromFile();
        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        nameEditText.setText(name);
    }

    public void saveSettings(View v){
        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        fileOperationsController.writeSettingsToFile(nameEditText.getText().toString());
    }


}
