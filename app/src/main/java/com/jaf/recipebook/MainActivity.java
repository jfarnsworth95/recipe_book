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

    public int storageOption;
    public String appFileDir;

    DirectoryHelper dh = new DirectoryHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Determine app file directory
        storageOption = dh.getStorageOption();
        if(storageOption == dh.STORAGE_EXTERNAL){
            Log.i("MainActivity", "EXTERNAL USED");
        } else {
            Log.i("MainActivity", "INTERNAL USED");
        }
        appFileDir = dh.getAppDirPath();

        //Run the rest of the setup
        displayPage();
    }

    /**
     * When you navigate away from the activity, then come back.
     * <p>Could be from leaving the application, or moving between activities</p>
     */
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        displayPage();
    }

    /**
     * Get and display info for main screen
     * <p>This includes all recipes, or a fallback screen if no recipes exist</p>
     */
    public void displayPage() {

        //Make sure App external folder exists if we are allowed external access
        if(storageOption == dh.STORAGE_EXTERNAL) {
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

        //Retrieve all file names from app folder and sort alphabetically (remove extension as well)
        String[] recipeTitles = getRecipeFiles();
        Arrays.sort(recipeTitles);
        Log.i("MainActivity", "Files under directory [" + appFileDir + "]:");

        if (recipeTitles == null || recipeTitles.length != 0) {
            //What to do if no recipes exist

            //Get ListView layout for inflating in data
            ListView listView = (ListView) findViewById(R.id.recipe_list_view);

            //inflate data from directories
            RecipeListAdapter adapter = new RecipeListAdapter(this, recipeTitles);
            listView.setAdapter(adapter);
        } else {
            //What to do if recipes exist in folder
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
        startActivity(intent);
    }

    /**
     * On click method for recipe list items
     * @param view Activity view
     */
    public void onRecipeClicked(View view){
        TextView tv = (TextView) view;
        String recipeName = tv.getText().toString();

        Intent intent = new Intent(view.getContext(), ViewRecipeActivity.class);
        intent.putExtra(RECIPE_VIEW,recipeName);
        startActivity(intent);
    }

    /**
     * You can find my sass comments over in ViewRecipeActivity.class for this construct
     * @param menu Trust me, you're not missing anything
     * @return Yep, definitely
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * What to do when setting item selected. There's only one at the time of writing this, so... yeah
     * @param item Settings. It's only settings
     * @return Yep, you selected Settings.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            View view = findViewById(android.R.id.content);
            Snackbar.make(view, "You clicked settings. Shame that doesn't do anything yet, huh?", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Verify app external folder exists. Create if not.
     */
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

    /**
     * Stop users from going back to the setup screen
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true); //Its that easy
    }

}