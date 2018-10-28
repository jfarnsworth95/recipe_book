package com.jaf.recipebook;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class EditRecipeActivity extends AppCompatActivity {

    final int FILE_CREATED = 0;
    final int FILE_CREATE_FAILED = 1;
    final int FILE_ALREADY_EXISTS = 2;
    final int FILE_WRITE_ERROR = -1;

    boolean isRecipeBeingEdited;
    String appFileDir;
    TagHelper tagHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        isRecipeBeingEdited = intent.getBooleanExtra(MainActivity.RECIPE_EDIT,true);
        appFileDir = intent.getStringExtra(MainActivity.APP_FILE_DIR);

        try {
            tagHelper = new TagHelper(new File(appFileDir));
        }catch (IOException ex){
            Snackbar.make(findViewById(android.R.id.content), "Failure interacting with the tag file",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    /**
     * Save Button listener
     */
    public void onSaveBtnClicked(View view){
        //TODO Add Tag file and ingredient file update
            //TODO check for removal of ingredients in edit and remove appropriately

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

            String[] tagList = tagsText.split(",");
            ArrayList<String> tags = new ArrayList<String>();
            for(String tag: tagList){
                tags.add(tag.replaceAll("^\\s*",""));
            }

            int fileStatus;

            fileStatus = createRecipeFile(titleText, ingredientsText, directionsText, tags);


            switch (fileStatus){
                case FILE_CREATED:
                    Log.i("EditRecipeActivity","File (" + titleText + ".csv) successfully created");
                    finishedEditing();
                    break;

                case FILE_CREATE_FAILED:
                    Log.e("EditRecipeActivity","File (" + titleText + ".csv) failed to create");
                    finishedEditing();
                    break;

                case FILE_ALREADY_EXISTS:
                    //Pass/Fail depends if file is being edited or not
                    if(isRecipeBeingEdited){
                        Log.i("EditRecipeActivity","File (" + titleText + ".csv) successfully edited");
                        finishedEditing();
                    } else {
                        Log.e("EditRecipeActivity","File (" + titleText + ".csv) already exists");
                        Snackbar.make(view, "A recipe with that title already exists", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    break;

                case FILE_WRITE_ERROR:
                    Log.e("EditRecipeActivity","Encountered IO Error while writing to file(" + titleText + ".csv)");
                    Snackbar.make(view, "Sorry, we couldn't add/edit that file. Check your permissions.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;

            }
        } else {
            Snackbar.make(view, "Fill out Title, Ingredients, and Directions to save", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    /**
     * Creates directory for recipe under main app folder in external storage
     * @param recipeTitle title of recipe
     * @return 0 if success, 1 if failure, 2 if file already exists
     */
    public int createRecipeFile(String recipeTitle, String recipeIngredients, String recipeDirections, ArrayList<String> tags){
        //Get file resources
        View view = findViewById(android.R.id.content);

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
                    modifyRecipeTags(recipeTitle,tags);
                    return FILE_CREATED;
                }
            } else {
                if(isRecipeBeingEdited) {
                    writeDataToFile(recipeFile, true, recipeIngredients, recipeDirections);
                    modifyRecipeTags(recipeTitle,tags);
                }
                return FILE_ALREADY_EXISTS;
            }
        } catch (IOException ex){
            return FILE_WRITE_ERROR;
        }
    }

    /**
     * Writes recipe data to file
     * @param file file to write to
     * @param shouldOverwriteFile true if file should be overwritten, otherwise will append
     * @param ingredients ingredients used in recipe
     * @param directions directions to make recipe
     * @throws IOException
     */
    public void writeDataToFile(File file, boolean shouldOverwriteFile,
                                String ingredients, String directions) throws IOException {
        FileWriter writer = new FileWriter(file.getPath(),shouldOverwriteFile);
        writer.write(ingredients + "\n");
        writer.write(getString(R.string.file_separator)+ "\n");
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
        if(titleEditText.getText().toString().equals("")){
            return false;
        }

        //Validate Ingredients filled out
        EditText ingredientsEditText = (EditText) findViewById(R.id.editText_recipe_ingredients);
        if(ingredientsEditText.getText().toString().equals("")){
            return false;
        }

        //Validate Directions filled out
        EditText directionsEditText = (EditText) findViewById(R.id.editText_recipe_directions);
        if(directionsEditText.getText().toString().equals("")){
            return false;
        }

        return true;
    }

    public void finishedEditing(){
        Intent quitEditIntent = new Intent(this, MainActivity.class);
        quitEditIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(quitEditIntent);
        finish();
    }

    public boolean modifyRecipeTags(String recipe, ArrayList<String> tags){
        if(isRecipeBeingEdited) {
            boolean removeSuccess = tagHelper.removeRecipe(recipe);
            if(!removeSuccess){
                return removeSuccess;
            }
        }
        return tagHelper.addTags(recipe,tags);
    }
}
