package com.jaf.recipebook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class Setup extends AppCompatActivity {

    public final static String STORAGE_OPTION = "com.jaf.recipebook.STORAGE_OPTION";
    final int PER_WRITE_EXTERNAL_STORAGE = 1;
    int grantForWrite = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //Run setup after 2 seconds (Lets UI Load)
        View view = findViewById(android.R.id.content);
        view.postDelayed(new Runnable(){
            public void run(){
                getPermissions();
            }
        },2000);
    }

    private void getPermissions(){

        DirectoryHelper dh = new DirectoryHelper(this);
        int storageAllowed = dh.getStorageOption();
        //Check & Request access (if not already granted) for individual permissions
        if (storageAllowed == dh.STORAGE_INTERNAL) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PER_WRITE_EXTERNAL_STORAGE);
        } else {
            goToMain();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PER_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    grantForWrite = 1;
                } else {
//                    String errorMsg = "This app will not work without this permission. No data of yours will be collected.";
//                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    grantForWrite = -1;
                }
            }
            // other 'case' lines to check for other permissions
        }
        if(grantForWrite == 1){
            //checkExternalDirectory();
            goToMain();
        } else if(grantForWrite == -1){
            goToMain();
        }
    }

    public void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
