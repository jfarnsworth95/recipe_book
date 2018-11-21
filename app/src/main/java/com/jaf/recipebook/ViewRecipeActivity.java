package com.jaf.recipebook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ViewRecipeActivity extends AppCompatActivity {

    public String appDirectory;
    private String recipeTitle;
    public final static String RECIPE_EDIT = MainActivity.RECIPE_EDIT;
    public final static String APP_FILE_DIR = MainActivity.APP_FILE_DIR;
    public final static String RECIPE_TITLE = "com.jaf.recipebook.RECIPE_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        Intent intent = getIntent();
        this.recipeTitle = intent.getStringExtra(MainActivity.RECIPE_VIEW);
        appDirectory = intent.getStringExtra(MainActivity.APP_FILE_DIR);
        File recipeFile = new File(appDirectory, recipeTitle + ".csv");

        try {
            //Get ingredients and directions from file
            BufferedReader br = new BufferedReader(new FileReader(recipeFile));
            String recipeIngredients = "";
            String recipeDirections = "";
            String st;

            //Get Ingredients
            while ((st = br.readLine()) != null) {
                if(!st.equals(getString(R.string.file_separator))){
                    recipeIngredients = recipeIngredients.concat(st + "\n");
                } else {
                    break;
                }
            }

            //If file ends without encountering separator
            if(!st.equals(getString(R.string.file_separator))){
                //TODO what do if file separator not present in file
            }

            //Get Directions
            while((st = br.readLine()) != null) {
                recipeDirections = recipeDirections.concat(st + "\n");
            }

            //Apply data to layout
            //Get text views
            TextView title_view = findViewById(R.id.view_recipe_title);
            TextView ingredients_view = findViewById(R.id.view_ingredient_list);
            TextView directions_view = findViewById(R.id.view_direction_text);
            //Set text
            title_view.setText(recipeTitle);
            ingredients_view.setText(recipeIngredients);
            directions_view.setText(recipeDirections);

        } catch (IOException ex){
            //TODO what do if file IO Exception
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_edit) {
            goToEditRecipe();
            finishActivity(0);
            return true;
        } else if (id == R.id.menu_item_delete) {
            View view = findViewById(android.R.id.content);
            Snackbar.make(view, "You clicked delete. Shame that doesn't do anything yet, huh?", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        } else {
            View view = findViewById(android.R.id.content);
            Snackbar.make(view, "I'm going to be perfectly honest here. I have no idea what you just pressed.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return super.onOptionsItemSelected(item);
        }
    }

    private void goToEditRecipe(){
        Intent intent = new Intent(findViewById(android.R.id.content).getContext(),
                EditRecipeActivity.class);
        intent.putExtra(RECIPE_EDIT,true);
        intent.putExtra(RECIPE_TITLE, recipeTitle);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            intent.putExtra(APP_FILE_DIR,Environment.getExternalStorageDirectory().getPath()
                    + "/" + getString(R.string.top_app_directory));
        } else {
            intent.putExtra(APP_FILE_DIR, getFilesDir().getPath());
        }

        startActivity(intent);
    }
}
