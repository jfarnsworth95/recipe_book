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

    private String recipeTitle;
    File recipeFile;

    private final String TAG = "ViewRecipeActivity";

    public final static String RECIPE_EDIT = MainActivity.RECIPE_EDIT;
    public final static String APP_FILE_DIR = MainActivity.APP_FILE_DIR;
    public final static String RECIPE_TITLE = "com.jaf.recipebook.RECIPE_TITLE";

    DirectoryHelper dh = new DirectoryHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        //Grab info needed for setup
        Intent intent = getIntent();
        this.recipeTitle = intent.getStringExtra(MainActivity.RECIPE_VIEW);
        recipeFile = new File(dh.getAppDirPath(), recipeTitle + ".csv");

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

            //Get text views
            TextView title_view = findViewById(R.id.view_recipe_title);
            TextView ingredients_view = findViewById(R.id.view_ingredient_list);
            TextView directions_view = findViewById(R.id.view_direction_text);

            //Set text
            title_view.setText(recipeTitle);
            ingredients_view.setText(recipeIngredients);
            directions_view.setText(recipeDirections);

        } catch (IOException ex){
            Log.e(TAG, "File read issue for: " + recipeTitle);
            finish();
        }
    }

    /**
     * Literally just creates the menu. Kinda in the name, as you might have noticed
     * @param menu The menu. Again, doesn't take a rocket scientist to figure that one out.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    /**
     * Determine action to take on item selection, options include:
     * <p>Delete</p>
     * <p>Edit</p>
     * @param item Item selected by user from menu
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Determine option selected
        if (id == R.id.menu_item_edit) {
            goToEditRecipe();
            finishActivity(0); //Removes activity from stack
            return true;
        } else if (id == R.id.menu_item_delete) {
            if(deleteRecipe()) {
                //Close activity only if success
                finish();
            }
            return true;
        } else {
            //Hey, I can have some fun right?
            View view = findViewById(android.R.id.content);
            Snackbar.make(view, "I'm going to be perfectly honest here. I have no idea what you just pressed.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Removes recipe file and clears tags from tag file
     */
    private boolean deleteRecipe() {

        try {

            boolean success = true;

            //Remove recipe file
            Log.i(TAG, "Deleting recipe: " + recipeTitle);
            boolean deleted = recipeFile.delete();

            //Remove tag file entry for recipe
            boolean tagDeleted = new TagHelper(new File(dh.getAppDirPath()),this).removeRecipe(recipeTitle);

            //Log details of attempt
            if(deleted){
                Log.i(TAG, recipeFile.getName() + " deleted");
            } else {
                Log.e(TAG, "Failure attempting to delete: " + recipeFile.getName());
                success = false;
            }
            if(tagDeleted){
                Log.i(TAG, recipeTitle + " tag deleted");
            } else {
                Log.e(TAG, "Failure attempting to delete tags for: " + recipeFile.getName());
                success = false;
            }

            return success;

        }catch (IOException ex){
            Snackbar.make(findViewById(android.R.id.content), "Failure interacting with the tag file",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
            Log.e(TAG, "Trouble with tag file when trying to delete: " + recipeTitle);
            return false;
        }
    }

    /**
     * Go to edit view, pass in recipe title, and notify that an edit action will occur
     */
    private void goToEditRecipe(){
        Intent intent = new Intent(findViewById(android.R.id.content).getContext(), EditRecipeActivity.class);
        intent.putExtra(RECIPE_EDIT,true);
        intent.putExtra(RECIPE_TITLE, recipeTitle);

        startActivity(intent);
    }
}
