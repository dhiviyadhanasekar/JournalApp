package com.dhiviyad.journalapp;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import com.dhiviyad.journalapp.constants.IntentFilterNames;
import com.dhiviyad.journalapp.constants.Permissions;
import com.dhiviyad.journalapp.controllers.JournalEntryController;
import com.dhiviyad.journalapp.controllers.StepsCountController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class EntryService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "EntryService";
    IEntryAidlInterface.Stub mBinder;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation = null;

    boolean recordingEntry;
    JournalEntryController journalEntryController;

    public EntryService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAIDLBinder();
        createController();
        initGooglePlayService();
        recordingEntry = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient != null) mGoogleApiClient.disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void initAIDLBinder() {
        mBinder = new IEntryAidlInterface.Stub() {
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString)
                    throws RemoteException {}
            @Override
            public void initNewEntry(){
                onInitNewEntry();
            }
            @Override
            public void sendEntryData(){
                sendEntryDataBroadcast();
            }
        };
    }



    @Override
    public void onConnected(Bundle connectionHint) {
        //getLastKnownLocation();
    }
    @Override
    public void onConnectionSuspended(int i) { }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    private void onInitNewEntry() {
        recordingEntry = true;
        journalEntryController.createNewEntry();
        getLastKnownLocation();
    }

    private void sendEntryDataBroadcast() {
//        Toast.makeText(this, recordingEntry + "" , Toast.LENGTH_LONG).show();
        Log.v(TAG, recordingEntry + " == recordingENtry");
        Intent i = new Intent();
        i.setAction(IntentFilterNames.ENTRY_DATA_RECEIVED);
        i.putExtra(IntentFilterNames.ENTRY_DATA,  journalEntryController.getEntry());
        sendBroadcast(i);
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
//            Toast.makeText(this, "lat = " + mLastLocation.getLatitude() + ", long = " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();

            double latitude  = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            journalEntryController.updateLocCoordinates(latitude, longitude);

            if(Geocoder.isPresent()){
                try {

                    Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
                    if (addresses.isEmpty()) {
                        Log.v(TAG, "No locations available 1");
//                        addres.setText("No locations available");
//                        Toast.makeText(this, "No locations available", Toast.LENGTH_LONG).show();
                    }
                    else {
                        if (addresses.size() > 0) {
                            String cityName = addresses.get(0).getLocality();
                            String stateName = addresses.get(0).getAdminArea();
                            String countryName = addresses.get(0).getCountryName();
                            journalEntryController.updateAddress(cityName, stateName, countryName);
//                            addres.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
//                            Toast.makeText(this, cityName + ", " + stateName + ", " + countryName, Toast.LENGTH_LONG).show();
                            Log.v(TAG, "Address = " + cityName + ", " + stateName + ", " + countryName);
                        } else {
                            Log.v(TAG, "No locations available 2");
//                            Toast.makeText(this, "No locations available 2", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace(); // getFromLocation() may sometimes fail
//                    addres.setText("Error");
//                    Toast.makeText(getApplicationContext(),"Error!!!", Toast.LENGTH_LONG).show();
                }
            }
            //todo: get city, state, country
        }
    }

    private void initGooglePlayService() {
        initLocationService();
        if(mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    private void initLocationService() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int status = googleAPI.isGooglePlayServicesAvailable(this);
        if(ConnectionResult.SUCCESS != status) {
            Log.e("EntryActivity", "GooglePlayServicesAvailable=false. ");
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void createController(){
        journalEntryController = new JournalEntryController(getApplicationContext());
    }


}
