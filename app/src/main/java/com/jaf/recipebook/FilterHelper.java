package com.jaf.recipebook;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

        //Compile into list and pass on
        String[] list = new String[matchingRecipesTags.size()];
        int counter = 0;
        for(String str: matchingRecipesTags){
            Log.i(TAG, "SEARCH RESULTS (TAG): " + str);
            list[counter] = str;
            counter ++;
        }
        return list;
    }

    private ArrayList<String> getRecipesWithMatchingTag(String query){
        File tagFile = new File(dh.getAppDirPath(), context.getString(R.string.tag_file_name));
        ArrayList<String> matchingRecipes = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(tagFile));
            String st;
            while ((st = br.readLine()) != null) {
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
