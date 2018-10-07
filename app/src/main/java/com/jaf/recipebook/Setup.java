package com.jaf.recipebook;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Setup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        boolean goodToGo = true;

        //Start up checks
        if(!checkPermissions()){
            goodToGo = false;
        }

        //If successful setup, start mainActivity, otherwise, return warning and close app
        if(goodToGo){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            String reasonsForFailure = "";
            reasonsForFailure.concat(getFailedPermissions());

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Here's what went wrong:\n" + reasonsForFailure)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Hard exit. Bad practice, but I need the app to close all the way.
                            System.exit(-1);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }



    /**
     * Get Permission Grants for application
     * @return true if all permissions have been granted, otherwise false.
     */
    protected boolean checkPermissions(){
        //Check & Request access (if not already granted) for individual permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        //Check if user granted them
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * Get all permissions that were not granted, separated with newline
     * @return String of permissions separated by newline
     */
    protected String getFailedPermissions() {
        String failedPermissions = "";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            failedPermissions.concat("External Storage Write Permission not granted\n");
        }

        return failedPermissions;
    }
}
