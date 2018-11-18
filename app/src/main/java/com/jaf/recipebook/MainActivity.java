package com.jaf.recipebook;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public final static String RECIPE_EDIT = "com.jaf.recipebook.RECIPE_EDIT";
    public final static String RECIPE_VIEW = "com.jaf.recipebook.RECIPE_VIEW";
    public final static String APP_FILE_DIR = "com.jaf.recipebook.APP_FILE_DIR";

    final int STORAGE_INTERNAL = 0;
    final int STORAGE_EXTERNAL = 1;
    public int storageOption;
    public String appFileDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            storageOption = STORAGE_INTERNAL;
        } else {
            storageOption = STORAGE_EXTERNAL;
        }

        Intent intent = getIntent();
        if(storageOption == STORAGE_EXTERNAL){
            appFileDir = Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.top_app_directory);
            Log.i("MainActivity", "EXTERNAL USED");
        } else {
            appFileDir = getFilesDir().getPath();
            Log.i("MainActivity", "INTERNAL USED");
        }
        displayPage();
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        displayPage();
    }

    public void displayPage() {

        if(storageOption == STORAGE_EXTERNAL) {
            checkExternalDirectory();
        }

        //Button to add recipes
        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.add_recipe_fab);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_add_onClick(view);
            }
        });

        String[] recipeTitles = getRecipeFiles();
        Arrays.sort(recipeTitles);
        Log.i("MainActivity", "Files under directory (" + appFileDir + "):");
        for(String title: recipeTitles){
            Log.i("MainActivity", "File name: "+ title);
        }

        if (recipeTitles == null || recipeTitles.length != 0) {
            //Get ListView layout for inflating in data
            ListView listView = (ListView) findViewById(R.id.recipe_list_view);

            //inflate data from directories
            RecipeListAdapter adapter = new RecipeListAdapter(this, recipeTitles);
            listView.setAdapter(adapter);
        } else {
            //Remove existing child view, replace with inflated layout
            View currentChildView = findViewById(R.id.recipe_list_view);
            if(currentChildView != null) {
                ViewGroup parent = (ViewGroup) currentChildView.getParent();

                //Inflate recipe_list_empty layout template
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View noRecipeView = inflater.inflate(R.layout.recipe_list_empty, parent, false);
                parent.removeView(currentChildView);
                parent.addView(noRecipeView);
            }
        }
    }

    /**
     * Get all directories directly under the main app directory in external storage
     * @return list of file names (Recipe Names)
     */
    public String[] getRecipeFiles() {

        File directory = new File(appFileDir);
        File[] staticFilesList = directory.listFiles();

        if(staticFilesList == null || staticFilesList.length == 0){
            return new String[] {};
        }

        ArrayList<File> files = new ArrayList<File>();
        //TODO: DO THIS BETTER. This was just a gap fix to determine the problem.
        for(File file: staticFilesList){
            if(!file.getName().contains(".csv")){ //Files this program didn't put there
                files.remove(file);
            }
            else if(file.getName().equals(getString(R.string.tag_file_name))){ //Tag file
                files.remove(file);
            } else {
                files.add(file);
            }
            //TODO Remove ingredients.csv from list
        }

        if(files.size() == 0){
            return new String[] {};
        }


        //Get String names and remove file extension from name
        String[] fileNames = new String[files.size()];
        for(int i = 0; i < files.size(); i ++){
           fileNames[i] = files.get(i).getName().replace(".csv", "");
        }
        return fileNames;
    }

    /**
     * Adds recipe, goes to new activity to allow user input
     * @param view
     */
    public void fab_add_onClick(View view) {
        Intent intent = new Intent(view.getContext(), EditRecipeActivity.class);
//        Intent intent = new Intent(view.getContext(), TestViewer.class);
        intent.putExtra(RECIPE_EDIT,false);
        intent.putExtra(APP_FILE_DIR, appFileDir);
        startActivity(intent);
    }

    public void onRecipeClicked(View view){
        TextView tv = (TextView) view;
        String recipeName = tv.getText().toString();

        Intent intent = new Intent(view.getContext(), ViewRecipeActivity.class);
        intent.putExtra(RECIPE_VIEW,recipeName);
        intent.putExtra(APP_FILE_DIR, appFileDir);
        startActivity(intent);
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
            View view = findViewById(android.R.id.content);
            Snackbar.make(view, "You clicked settings. Shame that doesn't do anything yet, huh?", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkExternalDirectory(){
        View view = findViewById(android.R.id.content);
        String mainDir = getString(R.string.top_app_directory);
        File f = new File(Environment.getExternalStorageDirectory(), mainDir);

        //Create main app directory if it doesn't exist
        if (!f.exists()) {
            if (!f.mkdir()) {
                //Failure
                Snackbar.make(view, "Failed to create main directory\n" + f, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}



//AlertDialog.Builder builder;
//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
//} else {
//    builder = new AlertDialog.Builder(context);
//}
//builder.setTitle("Delete entry")
//.setMessage("Are you sure you want to delete this entry?")
//.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//    public void onClick(DialogInterface dialog, int which) {
//        // continue with delete
//    }
//})
//.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//    public void onClick(DialogInterface dialog, int which) {
//        // do nothing
//    }
//})
//.setIcon(android.R.drawable.ic_dialog_alert)
//.show();