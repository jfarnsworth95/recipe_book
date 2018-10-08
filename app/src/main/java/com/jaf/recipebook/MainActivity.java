package com.jaf.recipebook;

import android.content.Context;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_onClick(view);
            }
        });

        listView = (ListView) findViewById(R.id.recipe_list_view);
        String[] recipeTitles = getRecipeDirs();

        if(recipeTitles.length != 0) {
            RecipeListAdapter adapter = new RecipeListAdapter(this,recipeTitles);
            listView.setAdapter(adapter);
        }
    }

    public String[] getRecipeDirs() {
        //Check external app dir for sub dirs
        return new String[0];
    }

    public void fab_onClick(View view) {
        Snackbar.make(view, "Soon, I'll add stuff", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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
