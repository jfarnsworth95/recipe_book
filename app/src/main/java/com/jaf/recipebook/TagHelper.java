package com.jaf.recipebook;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class TagHelper {

    private File tagFile;
    private File mainDir;
    private final String TAG = "TagHelper";

    /**
     * All methods should be called only after the main recipe file has been saved
     * <p>Helper class for modifying the tag file used for sorting on the main activity</p>
     * <p>Also used to return filtering of files</p>
     * <p>Tag file format:</p>
     * <p>RecipeName:,tag0,tag1,tag2\n</p>
     * @throws IOException Tag File read/write failed
     */
    protected TagHelper(File appFileDir, Context context) throws IOException {
        //Get tag file, create if it doesn't exist
        mainDir = appFileDir;
        tagFile = new File(mainDir, context.getString(R.string.tag_file_name));

        //Create main app directory if it doesn't exist
        if (!tagFile.exists()) {
            Log.i(TAG, "Tag file does not exist");
            if (!tagFile.createNewFile()) {
                tagFile = null;
                Log.e(TAG, "Failed to create tag file");
                throw new IOException("Couldn't make tag file");
            } else {
                Log.i(TAG, "Tag file created");
            }
        }
    }

    /**
     * Add tags for recipe to file
     * @param recipeTitle recipe to add to
     * @param tags tags to add with respect to recipe
     * @return Temp file successfully deleted
     */
    protected boolean addTags(String recipeTitle, ArrayList<String> tags){
        Log.i(TAG, "Adding tags for: " + recipeTitle);

        //Create temp file for swapping
        File tmpFile = new File(mainDir, "tmp.txt");

        //Set to lower case for easy matching
        for(int i = 0; i < tags.size(); i ++){
            tags.set(i,tags.get(i).toLowerCase());
        }

        try {
            //Write to temporary file to ensure no data loss
            FileWriter writer = new FileWriter(tmpFile.getPath(),false);

            BufferedReader br = new BufferedReader(new FileReader(tagFile));
            String st;

            while ((st = br.readLine()) != null) {
                writer.write(st + "\n");
            }

            //Add recipe in
            String newEntry = recipeTitle + ":";
            for(String tag: tags) {
                if (!newEntry.contains("," + tag)) { //Check if tag already exists, if so do nothing
                    //Add to recipe
                    newEntry = newEntry.concat("," + tag);
                }
            }
            Log.i(TAG, "Writing to temp file: " + newEntry);
            writer.write(newEntry + "\n");

            writer.close();
            br.close();

            //swap tmp and original tag file
            br = new BufferedReader(new FileReader(tmpFile));
            writer = new FileWriter(tagFile,false);
            while ((st = br.readLine()) != null) {
                //TODO Delete log statement after testing
                Log.i(TAG, "Writing from tmp to tagfile {" + st + "}");
                writer.write(st + "\n");
            }

            //Close to save
            br.close();
            writer.close();

            //delete tmp file
            return tmpFile.delete();

        } catch (IOException ex){
            Log.e(TAG, "IO Error while adding tags for [" + recipeTitle + "]");
            return false;
        }
    }

    /**
     * Remove recipe and it's tags from the tag file. Should happen along with recipe file deletion
     * @param recipeTitle recipe to delete
     * @return Temp file successfully deleted
     */
    protected boolean removeRecipe(String recipeTitle){
        //Create temp file for swapping
        File tmpFile = new File(mainDir, "tmp.txt");

        //Check if recipe exists in file
        try {
            FileWriter writer = new FileWriter(tmpFile.getPath(),false);

            BufferedReader br = new BufferedReader(new FileReader(tagFile));
            boolean recipeExists = false;
            String st;

            while ((st = br.readLine()) != null) {
                //Check if recipe exists in file
                if(!st.contains(recipeTitle + ":,")){ //Do not write if recipe title in line
                    writer.write(st + "\n");
                } else {
                    Log.i(TAG, "Removing recipe from tag file via omission [" + recipeTitle + "]");
                    recipeExists = true;
                }
            }
            writer.close();
            br.close();

            //swap tmp and original tag file
            br = new BufferedReader(new FileReader(tmpFile));
            writer = new FileWriter(tagFile,false);
            while ((st = br.readLine()) != null) {
                writer.write(st + "\n");
            }

            writer.close();
            br.close();

            //delete tmp file
            return tmpFile.delete();

        } catch (IOException ex){
            return false;
        }
    }

    /**
     * Used by filter to find matching recipes for the query
     * @param tag Partial match to check
     * @return List of matching recipes
     */
    public ArrayList<String> getRecipesWithTag(String tag){
        //Set to lower case for easy matching
        tag = tag.toLowerCase();

        ArrayList<String> matchingRecipes = new ArrayList<String>();

        //Check tag file line by line, save to list any recipe that has tag
        try {

            BufferedReader br = new BufferedReader(new FileReader(tagFile));
            String st;

            while ((st = br.readLine()) != null) {
                //Check if recipe exists in file
                if(st.contains(tag)){ //Do not write if recipe title in line
                    matchingRecipes.add(st.split(":")[0]);
                }
            }
            br.close();

            return matchingRecipes;

        } catch (IOException ex){
            return new ArrayList<String>();
        }
    }

    /**
     * Get tags for a given recipe
     * @param recipeTitle recipe to get tags from
     * @return list of tags from recipe
     */
    protected ArrayList<String> getTagsForRecipe(String recipeTitle){

        ArrayList<String> tags = new ArrayList<String>();

        //Check tag file line by line, save to list any recipe that has tag
        try {

            BufferedReader br = new BufferedReader(new FileReader(tagFile));
            String st;

            while ((st = br.readLine()) != null) {
                //Check if recipe exists in file
                if(st.contains(recipeTitle + ":")){ //Do not write if recipe title in line
                    String tagsAsString = st.split(":")[1];
                    for(String tag: tagsAsString.split(",")){
                        if(!tag.equals("") && !tags.contains(tag)) {
                            tags.add(tag);
                        }
                    }
                }
            }
            br.close();

            String tagsString = "";
            for(String tag: tags){
                tagsString = tagsString.concat(tag + ", ");
            }
            Log.i(TAG, "getTagsForRecipe: " + tagsString);

            return tags;

        } catch (IOException ex){
            return new ArrayList<String>();
        }
    }
}
