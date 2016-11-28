package com.dhiviyad.journalapp;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.os.RemoteException;

public class JournalRemoteService extends Service implements SensorEventListener {

    IJournalAidlInterface.Stub mBinder;

    public JournalRemoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAIDLBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void initAIDLBinder() {
        mBinder = new IJournalAidlInterface.Stub() {
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {}
            @Override
            public void startWorkout() { onStartWorkout(); }
            @Override
            public void stopWorkout() { onStopWorkout(); }
        };
    }

    private void onStopWorkout() {
    }

    private void onStartWorkout() {
    }
    
    
}
