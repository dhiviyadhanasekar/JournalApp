package com.dhiviyad.journalapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dhiviyad.journalapp.controllers.FileOperationsController;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class SettingsActivity extends AppCompatActivity {

    FileOperationsController fileOperationsController;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.activity_settings);
        if(fileOperationsController == null) {
            fileOperationsController = new FileOperationsController(getApplicationContext());
        }
        loadSettings();

        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("36BA870AF0AA46DCAEC9BF69760C6E25")
                .build();
        mAdView.loadAd(request);
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
