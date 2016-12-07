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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dhiviyad.journalapp.constants.AppConstants;
import com.dhiviyad.journalapp.constants.AppData;
import com.dhiviyad.journalapp.constants.IntentFilterNames;
import com.dhiviyad.journalapp.utils.DateUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    RemoteConnection remoteConnection;
    EntryRemoteConnection entryRemoteConnection;
    IEntryAidlInterface entryRemoteService;
    IJournalAidlInterface remoteService;
    boolean entryServiceConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entryServiceConnected =  false;
        bindService();
        bindEntryService();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if(entryServiceConnected == false){
                    Toast.makeText(getApplicationContext(), "Waiting for services to come up", Toast.LENGTH_LONG);
                }
                while(entryServiceConnected == false){};
                try {
                    entryRemoteService.initNewEntry();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(MainActivity.this, EntryActivity.class));
            }
        });

        if(AppData.getInstance().getDateSelected() == null){
            AppData.getInstance().setDateSelected(DateUtils.getCurrentDate());
        }

        LinearLayout mainView = (LinearLayout) findViewById(R.id.entriesView);
        for(int i=0; i< 3; i++) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View entryView = inflater.inflate(R.layout.entry_item, null);//(R.layout.entry_item, );
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.setMargins(0,10,0,10);
            entryView.setLayoutParams(buttonLayoutParams);
            final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new MyGestureDetector(entryView));
            entryView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
//                    Toast.makeText(MainActivity.this, "gesture detected ", Toast.LENGTH_SHORT).show();
                    return gestureDetector.onTouchEvent(event);
                }
            });
            mainView.addView(entryView);
        }

        registerBroadCastReceivers();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
     * GESTURES
     *******************************************/
    private final static int REL_SWIPE_MIN_DISTANCE = 0;
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        View view;

        public MyGestureDetector(View view){
            this.view = view;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG,"Fling detected");
            if(e1.getX() - e2.getX() >= REL_SWIPE_MIN_DISTANCE) {
                return onRTLFling( view);
            }  else {
                return onLTRFling(view);
            }
        }

    }

    private boolean onLTRFling(View view) {
        //hide menu
        View buttonLayout = view.findViewById(R.id.buttons);
        if(buttonLayout.isShown()) buttonLayout.setVisibility(View.GONE);
//        Toast.makeText(context,"hiihih Left to right " + list[pos].getName(),Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean onRTLFling(View view) {
        View buttonLayout = view.findViewById(R.id.buttons);
        if(!buttonLayout.isShown()) buttonLayout.setVisibility(View.VISIBLE);
//        Toast.makeText(context,"hiihih RTL " + list[pos].getName(),Toast.LENGTH_SHORT).show();
        return true;
    }
    /*******************************************
     * END OF GESTURES
     * *******************************************/



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
        intent.setClassName(AppConstants.PACKAGE_NAME, JournalRemoteService.class.getName());
        if(!getApplicationContext().bindService(intent, remoteConnection, BIND_AUTO_CREATE)){
            Toast.makeText(this, "failed to bind remote service", Toast.LENGTH_LONG).show();
//            isBound = false;
        } else {
//            isBound = true;
        }
    }
    class EntryRemoteConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            entryRemoteService = IEntryAidlInterface.Stub.asInterface((IBinder) service);
            Log.v(TAG, "remote service connected");
            Toast.makeText(MainActivity.this, "entry remote service connected", Toast.LENGTH_LONG).show();
            entryServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            entryRemoteService = null;
            Toast.makeText(MainActivity.this, "entry remote service disconnected", Toast.LENGTH_LONG).show();
            Log.v(TAG, "entry remote service disconnected");
        }
    }
    private void bindEntryService() {
        entryRemoteConnection = new EntryRemoteConnection();
        Intent intent = new Intent();
        intent.setClassName(AppConstants.PACKAGE_NAME, EntryService.class.getName());
        if(!getApplicationContext().bindService(intent, entryRemoteConnection, BIND_AUTO_CREATE)){
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
