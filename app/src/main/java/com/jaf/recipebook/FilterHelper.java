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
    public ArrayList<String> getRecipesWithQuery(String query){

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
                if(st.contains(query)){
                    //Get recipe name and add to matching list
                    matchingRecipes.add(st.split(":,")[0]);
                }
            }
        } catch (IOException ex){
            Log.e(TAG, "Failed to read tag file when searching for matching search query");
        }

        return matchingRecipes;
    }
}
