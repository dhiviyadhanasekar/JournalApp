package com.dhiviyad.journalapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Address;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dhiviyad.journalapp.constants.AppConstants;
import com.dhiviyad.journalapp.constants.AppData;
import com.dhiviyad.journalapp.constants.IntentFilterNames;
import com.dhiviyad.journalapp.constants.Permissions;
import com.dhiviyad.journalapp.controllers.JournalEntryController;
import com.dhiviyad.journalapp.models.JournalEntryData;
import com.dhiviyad.journalapp.utils.DateUtils;
import com.dhiviyad.journalapp.webservice.RemoteFetch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.util.ArrayList;

import layout.EntryHorizontalFragment;
import layout.EntryVerticalFragment;

public class EntryActivity extends AppCompatActivity {

    private static final String TAG = "EntryActivity";
    private static final int imageID = 1;

    private JournalEntryData journalEntry;// = new JournalEntryData();
    Fragment horizontalFragment = new EntryHorizontalFragment();
    Fragment verticalFragment = new EntryVerticalFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Travel Journal");
        setContentView(R.layout.activity_entry);

        bindService();

        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(this, "Google play services is not available - unable to fetch location", Toast.LENGTH_LONG).show();
//            return;
        } //else initLocationService();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    Permissions.LOCATION_SERVICE);
        }

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
        registerBroadCastReceivers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView description = (TextView) findViewById(R.id.descEditText);
        description.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                String text = mEdit.toString();
                if(remoteService != null){
                    try {
                        remoteService.saveDescription(text);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceivers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("onRequestPermisst", permissions.toString());
        switch (requestCode) {

            case Permissions.LOCATION_SERVICE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    getLastKnownLocation();
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

    private void showEntry(JournalEntryData entry) {
        if(entry == null) return;
        TextView txtView;
//        Toast.makeText(EntryActivity.this, "city = " + entry.getCityName(), Toast.LENGTH_LONG).show();
        txtView = (TextView) findViewById(R.id.locationTextView);
        if(entry.getCityName() != null){
            txtView.setText(entry.getCityName());
        } else txtView.setText("Data not yet available");
        txtView = (TextView) findViewById(R.id.dateTimeTextView);
        txtView.setText(entry.getDate() + " " + entry.getTime());
        String weather = "Waiting to get data";
        if(entry.getWeather() != null){
            weather = entry.getWeather();
        }
        txtView = (TextView) findViewById(R.id.weatherTextView);
        txtView.setText(weather);

        EditText descEditText = (EditText) findViewById(R.id.descEditText);
        descEditText.setText(entry.getDescription());
    }

    public void saveEntry(View v){
//        Toast.makeText(this, "Start Save", Toast.LENGTH_LONG).show();
        if(remoteService != null){
            try {
                remoteService.saveEntryData();
                Toast.makeText(EntryActivity.this, "Saved", Toast.LENGTH_LONG).show();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(EntryActivity.this, "Error - not saved", Toast.LENGTH_LONG).show();
            }
        }
    }


    /*******************************************
     * REMOTE CONNECTION
     * *******************************************/
    RemoteConnection remoteConnection;
    IEntryAidlInterface remoteService;
    class RemoteConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IEntryAidlInterface.Stub.asInterface((IBinder) service);
            Log.v(TAG, "remote service connected");
//            Toast.makeText(EntryActivity.this, "remote service connected", Toast.LENGTH_LONG).show();
            try {
                remoteService.sendEntryData();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            remoteService = null;
//            Toast.makeText(EntryActivity.this, "remote service disconnected", Toast.LENGTH_LONG).show();
            Log.v(TAG, "remote service disconnected");
        }
    }
    private void bindService() {
        remoteConnection = new RemoteConnection();
        Intent intent = new Intent();
        intent.setClassName(AppConstants.PACKAGE_NAME, EntryService.class.getName());
        if(!getApplicationContext().bindService(intent, remoteConnection, BIND_AUTO_CREATE)){
            Toast.makeText(this, "failed to bind remote service", Toast.LENGTH_LONG).show();
//            isBound = false;
        } else {
//            isBound = true;
        }
    }
    /*******************************************
     * END OF REMOTE CONNECTION
     *******************************************/

    /******************************************************
     * Broadcast service code
     ******************************************************/
    ArrayList<MyBroadcastReceiver> broadcastReceivers;
    class MyBroadcastReceiver extends BroadcastReceiver {
        public MyBroadcastReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch(action){
                case IntentFilterNames.ENTRY_DATA_RECEIVED:
//                    Toast.makeText(EntryActivity.this, "entry data received" , Toast.LENGTH_SHORT).show();
                    JournalEntryData entry = (JournalEntryData) intent.getSerializableExtra(IntentFilterNames.ENTRY_DATA);
                    showEntry(entry);
                    break;
//                case IntentFilterNames.STEPS_COUNT_RECEIVED:
//                    long stepsCountData = (long) intent.getLongExtra(IntentFilterNames.STEPS_COUNT_DATA, 0L);
//                    setStepCount(stepsCountData);
//                    Toast.makeText(MainActivity.this, "Firing onsensorchanged => " + stepsCountData , Toast.LENGTH_SHORT).show();
//                    break;


                default: break;
            }
        }
    }

    private void registerBroadCastReceivers(){
        broadcastReceivers = new ArrayList<MyBroadcastReceiver>();
        createBroadcaseReceiver(IntentFilterNames.ENTRY_DATA_RECEIVED);
    }
    private void createBroadcaseReceiver(String intentName){
        MyBroadcastReceiver r = new MyBroadcastReceiver();
        getApplicationContext().registerReceiver(r, new IntentFilter(intentName));
        broadcastReceivers.add(r);
    }
    private void unregisterBroadcastReceivers() {
        if(broadcastReceivers != null) {
            for (MyBroadcastReceiver br : broadcastReceivers) {
                getApplicationContext().unregisterReceiver(br);
            }
            broadcastReceivers = null;
        }
    }
    /******************************************************
     * End of Broadcast service code
     ******************************************************/
}
