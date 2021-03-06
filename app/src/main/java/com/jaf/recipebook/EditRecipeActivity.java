package com.jaf.recipebook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class EditRecipeActivity extends AppCompatActivity {

    final int FILE_CREATED = 0;
    final int FILE_CREATE_FAILED = 1;
    final int FILE_ALREADY_EXISTS = 2;
    final int FILE_WRITE_ERROR = -1;
    final String TAG = "EditRecipeActivity";

    boolean isRecipeBeingEdited;
    String appFileDir;

    TagHelper tagHelper;
    DirectoryHelper dh = new DirectoryHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        isRecipeBeingEdited = intent.getBooleanExtra(MainActivity.RECIPE_EDIT,true);

        appFileDir = dh.getAppDirPath();

        try {
            tagHelper = new TagHelper(new File(appFileDir),this);
        }catch (IOException ex){
            Snackbar.make(findViewById(android.R.id.content), "Failure interacting with the tag file",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        //Set Text if recipe already exists
        if(isRecipeBeingEdited){
            setEditFieldText(intent.getStringExtra(ViewRecipeActivity.RECIPE_TITLE));
        }
    }

    /**
     * If editing, use this to set all fields to current info for recipe
     * @param recipeTitle recipe to gather info from
     */
    private void setEditFieldText(String recipeTitle) {

        //Get fields
        EditText titleEditText = (EditText) findViewById(R.id.edit_text_recipe_title);
        EditText ingredientsEditText = (EditText) findViewById(R.id.editText_recipe_ingredients);
        EditText directionsEditText = (EditText) findViewById(R.id.editText_recipe_directions);
        EditText tagsEditText = (EditText) findViewById(R.id.editText_recipe_tags);

        //Get Ingredients and Directions from file
        String ingredients = "";
        String directions = "";
        try {
            File recipeFile = new File(appFileDir, recipeTitle + ".csv");
            BufferedReader br = new BufferedReader(new FileReader(recipeFile));
            String st;

            while (!(st = br.readLine()).equals(getString(R.string.file_separator))){
                ingredients = ingredients.concat(st + "\n");
            }
            ingredients = ingredients.replaceAll("\n$","");

            while((st = br.readLine()) != null){
                directions = directions.concat(st + "\n");
            }
            directions = directions.replaceAll("\n$","");

        } catch (IOException ex){
            Log.e(TAG, "Error attempting to read file [" + recipeTitle + ".csv]");
            finishActivity(-1);
        }

        //Get Tags from tag file
        ArrayList<String> tagList = tagHelper.getTagsForRecipe(recipeTitle);
        String tags = "";
        for (String tag: tagList){
            Log.i(TAG, "Tag for setting Edit Field: " + tag);
            tags = tags.concat(tag);
            if(tagList.indexOf(tag) != tagList.size() -1){
                tags = tags.concat(", ");
            }
        }

        //Set values
        titleEditText.setText(recipeTitle);
        ingredientsEditText.setText(ingredients);
        directionsEditText.setText(directions);
        tagsEditText.setText(tags);
    }

    /**
     * Save Button listener
     */
    public void onSaveBtnClicked(View view){

        //Check if fields are filled out
        if(areFieldsFilled()){
            //Get values
            EditText titleEditText = (EditText) findViewById(R.id.edit_text_recipe_title);
            EditText ingredientsEditText = (EditText) findViewById(R.id.editText_recipe_ingredients);
            EditText directionsEditText = (EditText) findViewById(R.id.editText_recipe_directions);
            EditText tagsEditText = (EditText) findViewById(R.id.editText_recipe_tags);

            String titleText = titleEditText.getText().toString();
            String ingredientsText = ingredientsEditText.getText().toString();
            String directionsText = directionsEditText.getText().toString();
            String tagsText = tagsEditText.getText().toString();

            Log.i(TAG, "\tTags: " + tagsText);

            //Get Tag field info and clean
            String[] tagList = tagsText.split(",");
            ArrayList<String> tags = new ArrayList<String>();
            for(String tag: tagList){
                Log.i(TAG, "Tag being added to list: " + tag);
                //remove leading whitespace
                String temp = tag.replaceAll("^\\s*","");
                tags.add(temp);
            }

            //Create/Modify file
            int fileStatus = createRecipeFile(titleText, ingredientsText, directionsText, tags);
            switch (fileStatus){
                case FILE_CREATED:
                    Log.i(TAG,"File (" + titleText + ".csv) successfully created");
                    finishedEditing();
                    break;

                case FILE_CREATE_FAILED:
                    Log.e(TAG,"File (" + titleText + ".csv) failed to create");
                    finishedEditing();
                    break;

                case FILE_ALREADY_EXISTS:
                    //Pass/Fail depends if file is being edited or not
                    if(isRecipeBeingEdited){
                        Log.i(TAG,"File (" + titleText + ".csv) successfully edited");
                        finishedEditing();
                    } else {
                        Log.e(TAG,"File (" + titleText + ".csv) already exists");
                        Snackbar.make(view, "A recipe with that title already exists", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    break;

                case FILE_WRITE_ERROR:
                    Log.e(TAG,"Encountered IO Error while writing to file(" + titleText + ".csv)");
                    Snackbar.make(view, "Sorry, we couldn't add/edit that file. Check your permissions.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;

            }
        } else {
            //User didn't fill in all necessary fields
            Snackbar.make(view, "Fill out Title, Ingredients, and Directions to save\n':' not allowed in title", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    /**
     * Creates directory for recipe under main app folder in external storage
     * @param recipeTitle title of recipe
     * @return 0 if success, 1 if failure, 2 if file already exists
     */
    public int createRecipeFile(String recipeTitle, String recipeIngredients, String recipeDirections, ArrayList<String> tags){
        File recipeFile = new File(appFileDir, recipeTitle + ".csv");

        // initiate media scan and put the new things into the path array to
        // make the scanner aware of the location and the files you want to see
        MediaScannerConnection.scanFile(this, new String[] {recipeFile.getPath().toString()}, null, null);

        //Create main app directory if it doesn't exist
        try {
            if (!recipeFile.exists()) {
                if (!recipeFile.createNewFile()) {
                    //Failure
                    return FILE_CREATE_FAILED;
                } else {
                    //Success
                    writeDataToFile(recipeFile, false, recipeIngredients, recipeDirections);
                    if(!modifyRecipeTags(recipeTitle,tags)){
                        Log.e(TAG, "Temp file not deleted");
                    }
                    return FILE_CREATED;
                }
            } else {
                if(isRecipeBeingEdited) {
                    writeDataToFile(recipeFile, true, recipeIngredients, recipeDirections);
                    if(!modifyRecipeTags(recipeTitle,tags)){
                        Log.e(TAG, "Temp file not deleted");
                    }
                }
                return FILE_ALREADY_EXISTS;
            }
        } catch (IOException ex){
            Log.w(TAG,"IO Error at line: " + Integer.toString(ex.getStackTrace()[0].getLineNumber()));
            return FILE_WRITE_ERROR;
        }
    }

    /**
     * Writes recipe data to file
     * @param file file to write to
     * @param shouldOverwriteFile true if file should be overwritten, otherwise will append
     * @param ingredients ingredients used in recipe
     * @param directions directions to make recipe
     * @throws IOException Couldn't write to recipe file
     */
    public void writeDataToFile(File file, boolean shouldOverwriteFile,
                                String ingredients, String directions) throws IOException {
        FileWriter writer = new FileWriter(file.getPath(),!shouldOverwriteFile);
        writer.write(ingredients + "\n");
        writer.write(getString(R.string.file_separator)+ "\n"); //File separator between ingredients and directions
        writer.write(directions);
        writer.close();
    }

    /**
     * Validate that all required fields are filled out:
     * <p>edit_text_recipe_title</p>
     * <p>editText_recipe_ingredients</p>
     * <p>editText_recipe_directions</p>
     * @return true if all required fields are filled out
     */
    public boolean areFieldsFilled(){
        //Validate Title filled out
        EditText titleEditText = (EditText) findViewById(R.id.edit_text_recipe_title);
        if(titleEditText.getText().toString().equals("") || titleEditText.getText().toString().contains(":")){
            return false;
        }

        //Validate Ingredients filled out
        EditText ingredientsEditText = (EditText) findViewById(R.id.editText_recipe_ingredients);
        if(ingredientsEditText.getText().toString().equals("")){
            return false;
        }

        //Validate Directions filled out
        EditText directionsEditText = (EditText) findViewById(R.id.editText_recipe_directions);
        return !directionsEditText.getText().toString().equals("");
    }

    /**
     * Move back to main activity, remove this activity from the stack
     */
    public void finishedEditing(){
        Intent quitEditIntent = new Intent(this, MainActivity.class);
        quitEditIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(quitEditIntent);
        finish();
    }

    /**
     * Add/modify tag file with new tags for recipe
     * @param recipe name of recipe to make changes to
     * @param tags tags to add in
     * @return was temporary file deleted?
     */
    public boolean modifyRecipeTags(String recipe, ArrayList<String> tags){
        if(isRecipeBeingEdited) {
            Log.i(TAG, "Removing recipe [" + recipe + "] from tag file");
            tagHelper.removeRecipe(recipe);
        }
        return tagHelper.addTags(recipe,tags);
    }
}
