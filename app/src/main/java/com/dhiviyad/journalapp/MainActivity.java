package com.dhiviyad.journalapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dhiviyad.journalapp.constants.AppData;
import com.dhiviyad.journalapp.constants.IntentFilterNames;
import com.dhiviyad.journalapp.utils.DateUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    RemoteConnection remoteConnection;
    IJournalAidlInterface remoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, EntryActivity.class));
            }
        });

        if(AppData.getInstance().getDateSelected() == null){
            AppData.getInstance().setDateSelected(DateUtils.getCurrentDate());
        }

        registerBroadCastReceivers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceivers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setStepCount(long stepCount){
        if(DateUtils.getCurrentDate().equalsIgnoreCase(AppData.getInstance().getDateSelected())){
            TextView stepsTxtView = (TextView) findViewById(R.id.steps_count);
            if(stepsTxtView != null){
                stepsTxtView.setText(stepCount + "");
            }
        }
    }


    /*******************************************
     * REMOTE CONNECTION
     * *******************************************/
    class RemoteConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IJournalAidlInterface.Stub.asInterface((IBinder) service);
            Log.v(TAG, "remote service connected");
            Toast.makeText(MainActivity.this, "remote service connected", Toast.LENGTH_LONG).show();
            if(DateUtils.getCurrentDate().equalsIgnoreCase(AppData.getInstance().getDateSelected())){
                try {
                    remoteService.sendStepsCount();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            remoteService = null;
            Toast.makeText(MainActivity.this, "remote service disconnected", Toast.LENGTH_LONG).show();
            Log.v(TAG, "remote service disconnected");
        }
    }
    private void bindService() {
        remoteConnection = new RemoteConnection();
        Intent intent = new Intent();
        intent.setClassName("com.dhiviyad.journalapp", JournalRemoteService.class.getName());
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
                case IntentFilterNames.STEPS_COUNT_RECEIVED:
                    long stepsCountData = (long) intent.getLongExtra(IntentFilterNames.STEPS_COUNT_DATA, 0L);
                    setStepCount(stepsCountData);
//                    Toast.makeText(MainActivity.this, "Firing onsensorchanged => " + stepsCountData , Toast.LENGTH_SHORT).show();
                    break;


                default: break;
            }
        }
    }
    private void registerBroadCastReceivers(){
        broadcastReceivers = new ArrayList<MyBroadcastReceiver>();
        createBroadcaseReceiver(IntentFilterNames.STEPS_COUNT_RECEIVED);
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
