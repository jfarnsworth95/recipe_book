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
     * @throws IOException
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
        } else {
            Log.i(TAG, "Tag file exists");
        }
    }

    protected boolean addTags(String recipeTitle, ArrayList<String> tags){
        //Create temp file for swapping
        File tmpFile = new File(mainDir, "tmp.txt");

        //Set to lower case for easy matching
        recipeTitle = recipeTitle.toLowerCase();
        for(int i = 0; i < tags.size(); i ++){
            tags.set(i,tags.get(i).toLowerCase());
        }

        try {
            FileWriter writer = new FileWriter(tmpFile.getPath(),false);

            BufferedReader br = new BufferedReader(new FileReader(tagFile));
            boolean recipeExists = false;
            String st;

            while ((st = br.readLine()) != null) {
                //Check if recipe exists in file
                if(st.contains(recipeTitle)){
                    recipeExists = true;
                    for(String tag: tags) {
                        if (!st.contains(tag)) { //Check if tag already exists, if so do nothing
                            //Add to recipe
                            st = st.replace("\n", "," + tag + "\n");
                        }
                    }
                }
                writer.write(st);
            }

            //Add recipe if missing
            if(!recipeExists){
                Log.i(TAG, "[" + recipeTitle + "] has no entry in tag file, adding.");
                String newEntry = recipeTitle + ":";
                for(String tag: tags) {
                    if (!newEntry.contains(tag)) { //Check if tag already exists, if so do nothing
                        //Add to recipe
                        newEntry = newEntry.concat("," + tag);
                    }
                }
                Log.i(TAG, "Writing: " + newEntry);
                writer.write(newEntry + "\n");
            }
            writer.close();
            br.close();

            //swap tmp and original tag file
            br = new BufferedReader(new FileReader(tmpFile));
            writer = new FileWriter(tagFile,false);
            Log.i(TAG, "Writing from tmp to tagfile");
            while ((st = br.readLine()) != null) {
                //TODO Delete log statement after testing
                Log.i(TAG, "Writing from tmp to tagfile: " + st);
                writer.write(st);
            }

            br.close();
            writer.close();

            //delete tmp file
            return tmpFile.delete();

        } catch (IOException ex){
            return false;
        }
    }

    protected boolean removeTag(String recipeTitle, ArrayList<String> tags){
        //Create temp file for swapping
        File tmpFile = new File(mainDir, "tmp.txt");

        //Set to lower case for easy matching
        recipeTitle = recipeTitle.toLowerCase();
        for(int i = 0; i < tags.size(); i ++){
            tags.set(i,tags.get(i).toLowerCase());
        }

        //Check if recipe exists in file
        try {
            FileWriter writer = new FileWriter(tmpFile.getPath(),false);

            BufferedReader br = new BufferedReader(new FileReader(tagFile));
            boolean recipeExists = false;
            String st;

            while ((st = br.readLine()) != null) {
                //Check if recipe exists in file
                if(st.contains(recipeTitle)){
                    recipeExists = true;
                    for(String tag: tags) {
                        if (st.contains(tag)) {
                            //remove tags from recipe
                            st = st.replace("," + tag, "");
                            recipeExists = true;
                        }
                    }
                }
                //check if any tags remain after removal
                if(st.split(recipeTitle + ":,").length == 0){
                    //No tags, do not write line
                    Log.i(TAG, "No tags remaining for [" + recipeTitle + "], omitting from file");
                } else {
                    writer.write(st);
                }
            }
            writer.close();
            br.close();

            //swap tmp and original tag file
            br = new BufferedReader(new FileReader(tmpFile));
            writer = new FileWriter(tagFile,false);
            while ((st = br.readLine()) != null) {
                writer.write(st);
            }

            //delete tmp file
            tmpFile.delete();
            return recipeExists;

        } catch (IOException ex){
            return false;
        }
    }

    protected boolean removeRecipe(String recipeTitle){
        //Create temp file for swapping
        File tmpFile = new File(mainDir, "tmp.txt");

        //Set to lower case for easy matching
        recipeTitle = recipeTitle.toLowerCase();

        //Check if recipe exists in file
        try {
            FileWriter writer = new FileWriter(tmpFile.getPath(),false);

            BufferedReader br = new BufferedReader(new FileReader(tagFile));
            boolean recipeExists = false;
            String st;

            while ((st = br.readLine()) != null) {
                //Check if recipe exists in file
                if(!st.contains(recipeTitle)){ //Do not write if recipe title in line
                    writer.write(st);
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
                writer.write(st);
            }

            //delete tmp file
            tmpFile.delete();
            return recipeExists;

        } catch (IOException ex){
            return false;
        }
    }

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
}
