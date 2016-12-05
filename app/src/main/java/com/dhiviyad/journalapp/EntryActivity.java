package com.dhiviyad.journalapp;

import android.location.Address;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dhiviyad.journalapp.constants.Permissions;
import com.dhiviyad.journalapp.controllers.JournalEntryController;
import com.dhiviyad.journalapp.controllers.LocationController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import layout.EntryHorizontalFragment;
import layout.EntryVerticalFragment;

public class EntryActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int imageID = 1;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    JournalEntryController journalEntryController = new JournalEntryController();
    Fragment horizontalFragment = new EntryHorizontalFragment();
    Fragment verticalFragment = new EntryVerticalFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Travel Journal");
        setContentView(R.layout.activity_entry);

        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(this, "Google play services is not available - unable to fetch location", Toast.LENGTH_LONG).show();
//            return;
        } else initLocationService();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment contentFragment = null;
        Configuration config = getResources().getConfiguration();

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            contentFragment = horizontalFragment;
        } else {
            contentFragment = verticalFragment;
        }
        contentFragment.setRetainInstance(true);

        fragmentTransaction.replace(R.id.fragment_placeholder, contentFragment);
        fragmentTransaction.commit();
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

    @Override
    protected void onStart() {
        if(mGoogleApiClient != null) mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient != null) mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) { getLastKnownLocation(); }

    @Override
    public void onConnectionSuspended(int i) { }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    Permissions.LOCATION_SERVICE);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(this, "lat = " + mLastLocation.getLatitude() + ", long = " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();

            double latitude  = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            LocationController locationAddress = new LocationController();
            if(Geocoder.isPresent()){
//                locationAddress.getAddressFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
//                        getApplicationContext(), new GeocoderHandler());
                TextView addres = (TextView) findViewById(R.id.locationTextView);
                try {

                    Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
                    if (addresses.isEmpty()) {
                        addres.setText("Waiting for Location");
                    }
                    else {
                        if (addresses.size() > 0) {
                            addres.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                            Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                    addres.setText("Error");
                    Toast.makeText(getApplicationContext(),"Error!!!", Toast.LENGTH_LONG).show();
                }
            }
            //todo: get city, state, country

//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    handleGeoCoderOuput(locationAddress);
                    break;
                default:
                    locationAddress = null;
            }

        }
    }

    private void handleGeoCoderOuput(String locationAddress) {
        Toast.makeText(EntryActivity.this, "address " + locationAddress, Toast.LENGTH_LONG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("onRequestPermiss", permissions.toString());
        switch (requestCode) {

            case Permissions.LOCATION_SERVICE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    loadImage();
                    getLastKnownLocation();
                }
                break;

            default:
                break;
        }
    }

    //on click photo, open gallery & select image
    public void onClickPhoto(View v){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, imageID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("onActivityResult", requestCode + " => " + data);
        if(requestCode == imageID && resultCode == RESULT_OK && data != null){
            //todo:
//            recipie.setImageUri(data.getData());
//            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
//            Log.v("addRecipieImage", permissionCheck + "");
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                        this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_MEDIA);
//            } else {
//                Log.v("loadImage", "from addRecipieImage");
//                loadImage();
//            }
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int status = googleAPI.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            googleAPI.getErrorDialog(this, status, 0).show();//getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
