package com.jaf.recipebook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_onClick(view);
            }
        });
    }

    public void fab_onClick(View view){

        //File publicStorageDir = Environment.getExternalStorageDirectory();
        String mainDir = "RecipeBook";
        File f = new File(Environment.getExternalStorageDirectory(), mainDir);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(view, "Permission not Granted", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Snackbar.make(view, "Permission Granted", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

//        if (!f.exists()) {
//            if (f.mkdir()) {
//                Snackbar.make(view, "Created Main Directory\n" + f, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            } else {
//                Snackbar.make(view, "Failed to create main directory\n" + f, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        } else {
//            Snackbar.make(view, "Directory already exists\n" + f, Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//        }
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
