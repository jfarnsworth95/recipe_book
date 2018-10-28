package com.jaf.recipebook;

import android.content.res.Resources;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class TagHelper {

    File tagFile;
    File mainDir;

    final int STORAGE_INTERNAL = 0;
    final int STORAGE_EXTERNAL = 1;

    /**
     * All methods should be called only after the main recipe file has been saved
     * <p>Helper class for modifying the tag file used for sorting on the main activity</p>
     * <p>Also used to return filtering of files</p>
     * @throws IOException
     */
    public TagHelper(File appFileDir) throws IOException {
        //Get tag file, create if it doesn't exist
        tagFile = new File(appFileDir, Resources.getSystem().getString(R.string.tag_file_name));

        //Create main app directory if it doesn't exist
        if (!tagFile.exists()) {
            if (!tagFile.mkdir()) {
                tagFile = null;
                throw new IOException("Couldn't make tag file");
            }
        }
    }

    public boolean addTags(String recipeTitle, ArrayList<String> tags){
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
                writer.write(recipeTitle + ":");
                for(String tag: tags) {
                    if (!st.contains(tag)) { //Check if tag already exists, if so do nothing
                        //Add to recipe
                        writer.write("," + tag);
                    }
                }
                writer.write("\n");
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
            return tmpFile.delete();

        } catch (IOException ex){
            return false;
        }
    }

    public boolean removeTag(String recipeTitle, ArrayList<String> tags){
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
                        }
                    }
                }
                //check if any tags remain after removal
                if(st.split(recipeTitle + ":,").length == 0){
                    //No tags, do not write line
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
            return tmpFile.delete();

        } catch (IOException ex){
            return false;
        }
    }

    public boolean removeRecipe(String recipeTitle){
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
            return tmpFile.delete();

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
