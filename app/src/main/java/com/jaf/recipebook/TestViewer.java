package com.jaf.recipebook;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TestViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_viewer);

        Intent intent = getIntent();
        String appFileDirName = intent.getStringExtra(MainActivity.APP_FILE_DIR);

        TextView tv = (TextView) findViewById(R.id.test_text_view);

        try {
            File file = new File(appFileDirName, getString(R.string.tag_file_name));

            BufferedReader br = new BufferedReader(new FileReader(file));
            String fileContents = "";

//            String st;
//            while ((st = br.readLine()) != null) {
//                fileContents = fileContents.concat(st);
//            }
//
//            tv.setText(fileContents);

        } catch(IOException ex){
            Snackbar.make(findViewById(android.R.id.content), "IO Exception", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
