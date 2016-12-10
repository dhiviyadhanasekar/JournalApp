package com.dhiviyad.journalapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import layout.EntryHorizontalFragment;
import layout.EntryVerticalFragment;

import static android.R.attr.width;
import static com.dhiviyad.journalapp.constants.Permissions.PERMISSION_READ_MEDIA;

public class EntryActivity extends AppCompatActivity {

    private static final String TAG = "EntryActivity";
    private static final int imageID = 1;

    private JournalEntryData journalEntry;// = new JournalEntryData();
    Fragment horizontalFragment = new EntryHorizontalFragment();
    Fragment verticalFragment = new EntryVerticalFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Travel Entry");
        setContentView(R.layout.activity_entry);

        bindService();

        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(this, "Google play services is not available - unable to fetch location", Toast.LENGTH_LONG).show();
//            return;
        } //else initLocationService();

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

            case PERMISSION_READ_MEDIA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    loadImage();
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

    private static Uri imageUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("onActivityResult", requestCode + " => " + data);
        if(requestCode == imageID && resultCode == RESULT_OK && data != null){
//            recipie.setImageUri(data.getData());
            imageUri = data.getData();
            int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.v("add entry image", permissionCheck + "");
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_MEDIA);
            } else {
                Log.v("loadImage", "from Entryactivity");
                loadImage();
            }
        }
    }

    private void loadImage() {
        Log.v("loadImage", "yayy");
        createAppDirectory();
        try {
            remoteService.setPicture(imageUri.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        ImageView imageView = (ImageView) findViewById(R.id.imageView);
//        imageView.setImageURI(imageUri);

    }

    private boolean createAppDirectory(){
        File folder = new File(Environment.getExternalStorageDirectory() + "/JournalApp");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        return success;
    }


//    void savefile(URI sourceuri)
//    {
//        String sourceFilename= sourceuri.getPath();
//        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath()+File.separatorChar+"abc.mp3";
//
//        BufferedInputStream bis = null;
//        BufferedOutputStream bos = null;
//
//        try {
//            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
//            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
//            byte[] buf = new byte[1024];
//            bis.read(buf);
//            do {
//                bos.write(buf);
//            } while(bis.read(buf) != -1);
//        } catch (IOException e) {
//
//        } finally {
//            try {
//                if (bis != null) bis.close();
//                if (bos != null) bos.close();
//            } catch (IOException e) {
//
//            }
//        }
//    }

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

        if(entry.getPicture() != null && entry.getPicture().length() > 0){

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageUri = Uri.parse(entry.getPicture());
//            imageView.getLayoutParams().height = (int) getResources().getDimension(R.dimen.recipieImageView_height);
//            imageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.recipieImageView_width);
//            try {
//                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                Bitmap bitmapsimplesize = Bitmap.createScaledBitmap(bm, (int) getResources().getDimension(R.dimen.recipieImageView_width)
//                        , (int) getResources().getDimension(R.dimen.recipieImageView_height), true);
//                bm.recycle();
//                imageView.setImageBitmap(bitmapsimplesize);
                imageView.setImageURI(imageUri);
//            }catch (Exception e){
//                e.printStackTrace();
//            }

//            Bitmap bitmapsimplesize = Bitmap.createScaledBitmap(bm, width, height, true);
//            bm.recycle();

//            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(myStream, false);
//            Bitmap region = decoder.decodeRegion(new Rect(10, 10, 50, 50), null);

//            ImageView imageView = (ImageView) findViewById(R.id.imageView);
////            ((BitmapDrawable)imageView.getDrawable()).getBitmap().recycle();
//            imageView.setImageURI(imageUri);
        }
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
