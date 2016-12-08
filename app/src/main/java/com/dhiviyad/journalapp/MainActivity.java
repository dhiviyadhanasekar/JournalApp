package com.dhiviyad.journalapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
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
import com.dhiviyad.journalapp.controllers.MainPageController;
import com.dhiviyad.journalapp.models.JournalEntryData;
import com.dhiviyad.journalapp.utils.DateUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    RemoteConnection remoteConnection;
    EntryRemoteConnection entryRemoteConnection;
    IEntryAidlInterface entryRemoteService;
    IJournalAidlInterface remoteService;
    boolean entryServiceConnected;
    ArrayList<View> entryViewsArr;
    MainPageController mainPageController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entryServiceConnected = false;
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
                if (entryServiceConnected == false) {
                    Toast.makeText(getApplicationContext(), "Waiting for services to come up", Toast.LENGTH_LONG);
                }
                while (entryServiceConnected == false) {};
                try {
                    entryRemoteService.initNewEntry();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(MainActivity.this, EntryActivity.class));
            }
        });

        updateDateViews();
        addEntriesToView();
        registerBroadCastReceivers();
    }

    private void updateDateViews() {
        if (AppData.getInstance().getDateSelected() == null) {
            AppData.getInstance().setDateSelected(DateUtils.getCurrentDate());
        }

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                onDateChangedByUsed(year, month, dayOfMonth);
            }
        });
        calendarView.setDate(DateUtils.getCalendarDateFromDateFormat(AppData.getInstance().getDateSelected()), true, true);
        calendarView.setMaxDate(DateUtils.getCalendarDateFromDateFormat(DateUtils.getCurrentDate()));
    }

    private void addEntriesToView() {
//        updateDateViews();
        LinearLayout mainView = (LinearLayout) findViewById(R.id.entriesView);
        if (entryViewsArr != null && entryViewsArr.size() > 0) {
            for (int i = 0; i < entryViewsArr.size(); i++) {
                mainView.removeView(entryViewsArr.get(i));
            }
        }

        entryViewsArr = new ArrayList<View>();

        if (mainPageController == null)
            mainPageController = new MainPageController(getApplicationContext());
        final ArrayList<JournalEntryData> entryData = mainPageController.getEntries(AppData.getInstance().getDateSelected());

        for (int i = 0; i < entryData.size(); i++) {

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View entryView = inflater.inflate(R.layout.entry_item, null);//(R.layout.entry_item, );

            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.setMargins(0, 10, 0, 10);
            entryView.setLayoutParams(buttonLayoutParams);

            final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new MyGestureDetector(entryView));
            entryView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
//                    Toast.makeText(MainActivity.this, "Touch detetced", Toast.LENGTH_SHORT).show();
                    return gestureDetector.onTouchEvent(event);
                }
            });

            final JournalEntryData currentEntryData = entryData.get(i);
            addDeleteButtonClick(entryView, currentEntryData);
            addEditButtonClick(entryView, currentEntryData);
            setEntryTextFields(entryView, currentEntryData);
            mainView.addView(entryView);
            entryViewsArr.add(entryView);
        }

        String textForHeader = "Entries for " + DateUtils.getEntriesHeaderDateFromDateFormat(AppData.getInstance().getDateSelected());
        if (entryViewsArr.size() <= 0) textForHeader += ": No entries found";
        TextView entriesHeader = (TextView) findViewById(R.id.entriesHeaderTextView);
        entriesHeader.setText(textForHeader);

        if (!DateUtils.getCurrentDate().equalsIgnoreCase(AppData.getInstance().getDateSelected())) {
            setStepsCountTextView(mainPageController.getStepCounts(AppData.getInstance().getDateSelected()));
        }

    }

    private void addEditButtonClick(View entryView, final JournalEntryData currentEntryData) {
        ImageButton editButton = (ImageButton) entryView.findViewById(R.id.pencilBtn);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    Log.v(TAG, "Clicked delete button for id :: " + currentEntryData.getId());
                Toast.makeText(getApplicationContext(), "onClick", Toast.LENGTH_SHORT).show();
                if (entryServiceConnected == false) {
                    Toast.makeText(getApplicationContext(), "Waiting for services to come up", Toast.LENGTH_LONG);
                }
                while (entryServiceConnected == false) {};
                try {
                    entryRemoteService.initExistingEntry( currentEntryData.getId()
                            , currentEntryData.getLatitude() == null ? -1 : currentEntryData.getLatitude()
                            , currentEntryData.getLongitude() == null ? -1 : currentEntryData.getLongitude()
                            , currentEntryData.getCountryName()
                            , currentEntryData.getStateName()
                            , currentEntryData.getCityName()
                            , currentEntryData.getWeather()
                            , currentEntryData.getPicture()
                            , currentEntryData.getTime()
                            , currentEntryData.getDate()
                            , currentEntryData.getDescription()
                            , currentEntryData.getTimestamp());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(MainActivity.this, EntryActivity.class));

            }
        });
    }

    private void addDeleteButtonClick(View entryView, final JournalEntryData currentEntryData) {
        ImageButton deleteButton = (ImageButton) entryView.findViewById(R.id.trashBin);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    Log.v(TAG, "Clicked delete button for id :: " + currentEntryData.getId());
                int count = mainPageController.deleteEntry(currentEntryData.getId());
                if (count > 0) {
                    Toast.makeText(getApplicationContext(), "Deleted " + count + " entry", Toast.LENGTH_SHORT).show();
                    addEntriesToView();
                }
//                    Toast.makeText(getApplicationContext(), "onClick", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEntryTextFields(View entryView, JournalEntryData currentEntryData) {
        TextView txtView = (TextView) entryView.findViewById(R.id.timeView);
        txtView.setText(currentEntryData.getTime());
        txtView = (TextView) entryView.findViewById(R.id.descView);
        String desc = currentEntryData.getDescription();
        int maxCountLetters = 30;
        desc = desc.substring(0, Math.min(desc.length(), maxCountLetters)) + ((desc.length() > maxCountLetters) ? "..." : "");
        txtView.setText(desc);
    }

    private void onDateChangedByUsed(int year, int month, int dayOfMonth) {
        month++; //month from calendar view is 0 indexed
        String d = DateUtils.getDateFromCalendarViewDate(dayOfMonth, month, year);
//        if( d.equalsIgnoreCase(AppData.getInstance().getDateSelected())) return;
        AppData.getInstance().setDateSelected(d);
        Log.v(TAG, "dateeeeeeeeeee" + d);
        updateDateViews();
        addEntriesToView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        addEntriesToView();
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

    private void setStepCount(long stepCount) {
        if (DateUtils.getCurrentDate().equalsIgnoreCase(AppData.getInstance().getDateSelected())) {
            setStepsCountTextView(stepCount);
        }
    }

    private void setStepsCountTextView(long stepCount) {
        TextView stepsTxtView = (TextView) findViewById(R.id.steps_count);
        if (stepsTxtView != null) {
            stepsTxtView.setText(stepCount + "");
        }
    }

    /*******************************************
     * GESTURES
     *******************************************/
    private final static int REL_SWIPE_MIN_DISTANCE = 0;
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        View view;

        public MyGestureDetector(View view) {
            this.view = view;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "Fling detected");
            if (e1.getX() - e2.getX() >= REL_SWIPE_MIN_DISTANCE) {
                return onRTLFling(view);
            } else {
                return onLTRFling(view);
            }
        }

    }

    private boolean onLTRFling(View view) {
        //hide menu
        View buttonLayout = view.findViewById(R.id.buttons);
        if (buttonLayout.isShown()) buttonLayout.setVisibility(View.GONE);
//        Toast.makeText(context,"hiihih Left to right " + list[pos].getName(),Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean onRTLFling(View view) {
        View buttonLayout = view.findViewById(R.id.buttons);
        if (!buttonLayout.isShown()) {
            buttonLayout.setVisibility(View.VISIBLE);
        }
//        Toast.makeText(context,"hiihih RTL " + list[pos].getName(),Toast.LENGTH_SHORT).show();
        return true;
    }
    /*******************************************
     * END OF GESTURES
     * *******************************************/


    /*******************************************
     * REMOTE CONNECTION
     *******************************************/
    class RemoteConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IJournalAidlInterface.Stub.asInterface((IBinder) service);
            Log.v(TAG, "remote service connected");
            Toast.makeText(MainActivity.this, "remote service connected", Toast.LENGTH_LONG).show();
            if (DateUtils.getCurrentDate().equalsIgnoreCase(AppData.getInstance().getDateSelected())) {
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
        if (!getApplicationContext().bindService(intent, remoteConnection, BIND_AUTO_CREATE)) {
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
        if (!getApplicationContext().bindService(intent, entryRemoteConnection, BIND_AUTO_CREATE)) {
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
        public MyBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case IntentFilterNames.STEPS_COUNT_RECEIVED:
                    long stepsCountData = (long) intent.getLongExtra(IntentFilterNames.STEPS_COUNT_DATA, 0L);
                    setStepCount(stepsCountData);
//                    Toast.makeText(MainActivity.this, "Firing onsensorchanged => " + stepsCountData , Toast.LENGTH_SHORT).show();
                    break;


                default:
                    break;
            }
        }
    }

    private void registerBroadCastReceivers() {
        broadcastReceivers = new ArrayList<MyBroadcastReceiver>();
        createBroadcaseReceiver(IntentFilterNames.STEPS_COUNT_RECEIVED);
    }

    private void createBroadcaseReceiver(String intentName) {
        MyBroadcastReceiver r = new MyBroadcastReceiver();
        getApplicationContext().registerReceiver(r, new IntentFilter(intentName));
        broadcastReceivers.add(r);
    }

    private void unregisterBroadcastReceivers() {
        if (broadcastReceivers != null) {
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
