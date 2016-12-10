package com.dhiviyad.journalapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhiviyad.journalapp.constants.IntentFilterNames;
import com.dhiviyad.journalapp.controllers.LocationListController;

import java.util.ArrayList;

public class CitiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        LocationListController locationListController = new LocationListController(getApplicationContext());
        ArrayList<String> countries;
        Intent intent = getIntent();
        String cName = null;
        if(intent.hasExtra(IntentFilterNames.COUNTRY_NAME_DATA)){
            cName = intent.getStringExtra(IntentFilterNames.COUNTRY_NAME_DATA);
        }
        countries = locationListController.getCitiesVisited(cName);

        setTitle("Cities visited (" + countries.size() +")");

//        Toast.makeText(getApplicationContext(), "Countries => " + countries.get(0), Toast.LENGTH_LONG).show();
        LinearLayout mainView = (LinearLayout) findViewById(R.id.countriesView);

        for(int i=0; i<countries.size(); i++){

            final String countryName = countries.get(i);
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View countryView = inflater.inflate(R.layout.location_item, null);//(R.layout.entry_item, );
            countryView.setBackgroundColor(locationListController.getColorForRowPos(i));

            TextView countryTextView = (TextView) countryView.findViewById(R.id.countryName);
            countryTextView.setText(countryName);

            mainView.addView(countryView);
        }

        if(countries.size() == 0){
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View countryView = inflater.inflate(R.layout.location_item, null);//(R.layout.entry_item, );
            TextView countryTextView = (TextView) countryView.findViewById(R.id.countryName);
            countryTextView.setText("No countries visited yet");
        }
    }
}
