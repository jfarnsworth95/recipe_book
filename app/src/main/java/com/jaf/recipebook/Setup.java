package com.jaf.recipebook;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Setup extends AppCompatActivity {

    public final static String STORAGE_OPTION = "com.jaf.recipebook.STORAGE_OPTION";
    final int GOOGLE_SIGN_IN_ACTIVITY = 1000; //Int that represents google sign in activity
    final int GOOGLE_SERVICES_CHECK = 1001; //Int that represents google service error dialog
    final int PER_WRITE_EXTERNAL_STORAGE = 1;
    int grantForWrite = 0;

    final String TAG = "Setup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //Run setup after 2 seconds (Lets UI Load)
        View view = findViewById(android.R.id.content);
        view.postDelayed(new Runnable(){
            public void run(){
                getPermissions();
                accessGoogleDrive();
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

    /**
     * To allow Google Drive integration, check that Google Play API is up to date/active
     */
    public void accessGoogleDrive(){

        //Check if Google Play Services is active
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int returnCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        Dialog errorDialog = googleApiAvailability.getErrorDialog(this,returnCode,GOOGLE_SERVICES_CHECK);
        if(errorDialog != null) { //null if success
            errorDialog.show(); //Request user to update/install services
        }

        //Request access to Google Drive
        // This account must have the necessary scopes to make the API call
        // See https://developers.google.com/identity/sign-in/android/
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        //What do if account is null?
        if(account == null){
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            //Sign in to Google Services
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_ACTIVITY);
        }

        // Get the app's Drive folder
        DriveResourceClient client = Drive.getDriveResourceClient(this, account);
        client.getAppFolder()
        .addOnCompleteListener(this, new OnCompleteListener<DriveFolder>() {
            @Override
            public void onComplete(@NonNull Task<DriveFolder> task) {
                //Do stuff
            }
        });
    }
}
