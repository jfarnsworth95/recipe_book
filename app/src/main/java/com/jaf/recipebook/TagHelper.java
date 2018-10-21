package com.jaf.recipebook;

import android.content.res.Resources;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class TagHelper {

    File tagFile;

    /**
     * All methods should be called only after the main recipe file has been saved
     * <p>Helper class for modifying the tag file used for sorting on the main activity</p>
     * <p>Also used to return filtering of files</p>
     * @throws IOException
     */
    public TagHelper() throws IOException {
        //Get tag file, create if it doesn't exist
        String mainDirName = Resources.getSystem().getString(R.string.top_app_directory);
        File mainDir = new File(Environment.getExternalStorageDirectory(), mainDirName);
        tagFile = new File(mainDir, Resources.getSystem().getString(R.string.tag_file_name));

        //Create main app directory if it doesn't exist
        if (!tagFile.exists()) {
            if (!tagFile.mkdir()) {
                tagFile = null;
                throw new IOException("Couldn't make tag file");
            }
        }
    }

    public void addTag(String recipeTitle, String tag){
        //Set to lower case for easy matching

        //Check if recipe exists in file

        //Add recipe if missing

        //Add to recipe

        //Save
    }

    public void removeTag(String recipeTitle, String tag){
        //Set to lower case for easy matching

        //Check if recipe exists in file

        //Check if this is last tag existing for recipe

        //check if tag still exists as ingredient or tag


    }

    public void removeRecipe(String recipeTitle){
        //Set to lower case for easy matching

        //Check if recipe exists in file

        //Remove recipe
    }

    public String[] getRecipesWithTag(String tag){
        //Set to lower case for easy matching

        //Check tag file line by line, save to list any recipe that has tag
        return new String[] {""};
    }
}
