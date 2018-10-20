package com.jaf.recipebook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public final static String RECIPE_EDIT = "com.jaf.recipebook.RECIPE_EDIT";
    public final static String RECIPE_VIEW = "com.jaf.recipebook.RECIPE_VIEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayPage();
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        displayPage();
    }

    public void displayPage() {
        checkExternalDirectory();

        //Button to add recipes
        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.add_recipe_fab);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_add_onClick(view);
            }
        });

        String[] recipeTitles = getRecipeFiles();

        if (recipeTitles == null || recipeTitles.length != 0) {
            //Get ListView layout for inflating in data
            ListView listView = (ListView) findViewById(R.id.recipe_list_view);

            //inflate data from directories
            RecipeListAdapter adapter = new RecipeListAdapter(this, recipeTitles);
            listView.setAdapter(adapter);
        } else {
            //Remove existing child view, replace with inflated layout
            View currentChildView = findViewById(R.id.recipe_list_view);
            ViewGroup parent = (ViewGroup) currentChildView.getParent();

            //Inflate recipe_list_empty layout template
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View noRecipeView = inflater.inflate(R.layout.recipe_list_empty, parent, false);
            parent.removeView(currentChildView);
            parent.addView(noRecipeView);
        }
    }

    /**
     * Get all directories directly under the main app directory in external storage
     * @return list of file names (Recipe Names)
     */
    public String[] getRecipeFiles() {
        String path = Environment.getExternalStorageDirectory().toString()+"/"
                + getString(R.string.top_app_directory);

        File directory = new File(path);
        File[] files = directory.listFiles();

        if(files == null || files.length == 0){
            String[] none = {};
            return none;
        }

        //TODO Remove tags.csv and ingredients.csv from list


        //Get String names and remove file extension from name
        String[] fileNames = new String[files.length];
        for(int i = 0; i < files.length; i ++){
           fileNames[i] = files[i].getName().replace(".csv", "");
        }
        return fileNames;
    }

    /**
     * Adds recipe, goes to new activity to allow user input
     * @param view
     */
    public void fab_add_onClick(View view) {
        Intent intent = new Intent(view.getContext(), EditRecipeActivity.class);
        intent.putExtra(RECIPE_EDIT,false);
        startActivity(intent);
    }

    public void onRecipeClicked(View view){
        TextView tv = (TextView) view;
        String recipeName = tv.getText().toString();

//        Intent intent = new Intent(view.getContext(), ViewTest.class);
//        intent.putExtra(RECIPE_VIEW,recipeName);
//        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkExternalDirectory(){
        View view = findViewById(android.R.id.content);
        String mainDir = getString(R.string.top_app_directory);
        File f = new File(Environment.getExternalStorageDirectory(), mainDir);

        //Create main app directory if it doesn't exist
        if (!f.exists()) {
            if (!f.mkdir()) {
                //Failure
                Snackbar.make(view, "Failed to create main directory\n" + f, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}

//Adapter for ListView
class RecipeListAdapter extends ArrayAdapter<String>{

    Context context;
    String[] title;

    public RecipeListAdapter(@NonNull Context context, String[] titles) {
        super(context, R.layout.recipe_list_template, R.id.list_view_title, titles);
        this.context = context;
        this.title = titles;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.recipe_list_template,parent,false);

        TextView titles = v.findViewById(R.id.list_view_title);
        titles.setText(title[position]);

        return v;
    }
}
