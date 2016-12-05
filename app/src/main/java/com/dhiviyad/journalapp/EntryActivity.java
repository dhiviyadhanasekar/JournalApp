package com.dhiviyad.journalapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dhiviyad.journalapp.controllers.JournalEntryController;

import layout.EntryHorizontalFragment;
import layout.EntryVerticalFragment;

public class EntryActivity extends AppCompatActivity {

    private static final int imageID = 1;

    JournalEntryController journalEntryController = new JournalEntryController();
    Fragment horizontalFragment = new EntryHorizontalFragment();
    Fragment verticalFragment = new EntryVerticalFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Travel Journal");
        setContentView(R.layout.activity_entry);

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
}
