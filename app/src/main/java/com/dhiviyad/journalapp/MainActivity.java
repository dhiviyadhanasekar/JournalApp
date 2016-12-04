package com.dhiviyad.journalapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.RemoteConnection;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    RemoteConnection remoteConnection;
    IJournalAidlInterface remoteService;
//
//    /*******************************************
//     * REMOTE CONNECTION
//     *******************************************/
    class RemoteConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IJournalAidlInterface.Stub.asInterface((IBinder) service);
            Log.v(TAG, "remote service connected");
            Toast.makeText(MainActivity.this, "remote service connected", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            remoteService = null;
            Toast.makeText(MainActivity.this, "remote service disconnected", Toast.LENGTH_LONG).show();
            Log.v(TAG, "remote service disconnected");
        }
    }
//
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
//    /*******************************************
//     * END OF REMOTE CONNECTION
//     *******************************************/

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


}
