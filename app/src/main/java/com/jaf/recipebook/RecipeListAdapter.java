package com.jaf.recipebook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class RecipeListAdapter extends ArrayAdapter<String> {

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