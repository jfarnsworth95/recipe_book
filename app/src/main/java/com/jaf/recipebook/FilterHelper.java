package com.jaf.recipebook;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class FilterHelper {

    DirectoryHelper dh;
    Context context;

    final String TAG = "FilterHelper";

    public FilterHelper(Context context){
        this.context = context;
        dh = new DirectoryHelper(context);
    }

    /**
     * Get all recipes that have a tag matching the query provided
     * @param query String to match against
     * @return
     */
    public String[] getRecipesWithQuery(String query){
        query = query.toLowerCase();

        //Get matching tags/recipes
        ArrayList<String> matchingRecipesTags = getRecipesWithMatchingTag(query);

        //Get matching recipe titles
        ArrayList<String> matchingRecipes = getMatchingRecipeTitles(query);

        //Compile into list and pass on
        String[] list = new String[matchingRecipesTags.size() + matchingRecipes.size()];
        int counter = 0;
        for(String str: matchingRecipesTags){
            list[counter] = str;
            counter ++;
        }
        for(String str: matchingRecipes){
            list[counter] = str;
            counter ++;
        }
        return list;
    }

    private ArrayList<String> getMatchingRecipeTitles(String query) {
        File directory = new File(dh.getAppDirPath());
        File[] staticFilesList = directory.listFiles();

        if(staticFilesList == null || staticFilesList.length == 0){
            return new ArrayList<String>();
        }

        ArrayList<File> files = new ArrayList<File>();
        for(File file: staticFilesList){
            if(!file.getName().contains(".csv")){ //Files this program didn't put there
                files.remove(file);
            }
            else if(file.getName().equals(context.getString(R.string.tag_file_name))){ //Tag file
                files.remove(file);
            } else {
                files.add(file);
            }
        }

        if(files.size() == 0){
            return new ArrayList<String>();
        }


        //Get String names and remove file extension from name
        ArrayList<String> fileNames = new ArrayList<String>();
        for(File file: files){
            if(file.getName().toLowerCase().contains(query)) {
                fileNames.add(file.getName().replace(".csv", ""));
            }
        }
        return fileNames;
    }

    private ArrayList<String> getRecipesWithMatchingTag(String query){
        File tagFile = new File(dh.getAppDirPath(), context.getString(R.string.tag_file_name));
        ArrayList<String> matchingRecipes = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(tagFile));
            String st;
            while ((st = br.readLine()) != null) {
//                //If matches  recipeName:(Any number of ,tag) (Any number of chars) query
//                // (Any number of chars), (Any number of anything)
//                if(st.matches("[a-zA-Z0-9_\\-\\s]*:,([a-zA-Z0-9_\\-\\s],)*" +
//                        "[a-zA-Z0-9_\\-\\s]*" + query + "[a-zA-Z0-9_\\-\\s]*," + ".*")){
                if(st.toLowerCase().contains(query)){
                    //Get recipe name and add to matching list
                    matchingRecipes.add(st.split(":,")[0]);
                }
            }
            return matchingRecipes;
        } catch (IOException ex){
            Log.e(TAG, "Failed to read tag file when searching for matching search query");
            return new ArrayList<String>();
        }
    }
}
