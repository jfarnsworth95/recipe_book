package com.jaf.recipebook;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class DirectoryHelper {

    final int STORAGE_INTERNAL = 0;
    final int STORAGE_EXTERNAL = 1;
    final String TAG = "DirectoryHelper";
    private Context context;

    /**
     * Reduce code maintenance by using this class to find storage option used and app file path
     * @param context The active activity context
     */
    DirectoryHelper(Context context){
        this.context = context;
    }

    /**
     * Get storage option available to the application
     * @return Internal or External storage allowed (class static values)
     */
    int getStorageOption(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            return STORAGE_INTERNAL;
        } else {
            return STORAGE_EXTERNAL;
        }
    }

    /**
     * Get path to application directory
     * @return Directory path
     */
    String getAppDirPath(){
        switch(getStorageOption()){
            case STORAGE_EXTERNAL: return Environment.getExternalStorageDirectory().getPath() + "/" +
                    context.getString(R.string.top_app_directory);

            case STORAGE_INTERNAL: return context.getFilesDir().getPath();

            default:
                Log.e(TAG, "Unknown storage option");
                return "";
        }
    }

}
