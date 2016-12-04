package com.dhiviyad.journalapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.dhiviyad.journalapp.IJournalAidlInterface;
import com.dhiviyad.journalapp.controllers.StepsCountController;

public class JournalRemoteService extends Service implements SensorEventListener {

    private static final String TAG = "JournalRemoteService";

    IJournalAidlInterface.Stub mBinder;
    private SensorManager sensorManager;
    StepsCountController stepsCountController = null;

    public JournalRemoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAIDLBinder();
        createController();
        createStepSensor();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //todo: save steps count to db
        Log.v(TAG, "Remote service onDestroy called");
        sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            long stepsCount = stepsCountController.processStepDetected();
            Toast.makeText(this, "Firing onsensorchanged => " + stepsCount , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void initAIDLBinder() {
        mBinder = new IJournalAidlInterface.Stub() {
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString)
                    throws RemoteException {}
            @Override
            public void startEntryWriting() {
//                onStartWorkout();
            }
//            @Override
//            public void stopEntryWriting() { onStopWorkout(); }
        };
    }

    private void createStepSensor(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = sensorManager.getDefaultSensor( Sensor.TYPE_STEP_DETECTOR );
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void createController(){
        stepsCountController = new StepsCountController(getApplicationContext());
    }
}
