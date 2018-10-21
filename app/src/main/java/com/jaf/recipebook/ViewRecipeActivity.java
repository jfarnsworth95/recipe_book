package com.jaf.recipebook;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ViewRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        Intent intent = getIntent();
        String recipeTitle = intent.getStringExtra(MainActivity.RECIPE_VIEW);

        String mainDir = getString(R.string.top_app_directory);
        File appDirectory = new File(Environment.getExternalStorageDirectory(), mainDir);
        File recipeFile = new File(appDirectory, recipeTitle + ".csv");

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

            //Apply data to layout
            //Get text views
            TextView title_view = findViewById(R.id.view_recipe_title);
            TextView ingredients_view = findViewById(R.id.view_ingredient_list);
            TextView directions_view = findViewById(R.id.view_direction_text);
            //Set text
            title_view.setText(recipeTitle);
            ingredients_view.setText(recipeIngredients);
            directions_view.setText(recipeDirections);

        } catch (IOException ex){
            //TODO what do if file IO Exception
        }
    }
}
