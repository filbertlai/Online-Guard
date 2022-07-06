package com.filbert.online_guard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NewsViewAdapter extends ArrayAdapter<NewsView>{

    public NewsViewAdapter(@NonNull Context context, ArrayList<NewsView> arraylist){
        super(context, 0, arraylist);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View currentItemView = convertView;

        if (currentItemView == null){
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_listview, parent, false);
        }

        NewsView currentNumberPosition = getItem(position);

        TextView title = currentItemView.findViewById(R.id.news_title);
        title.setText(currentNumberPosition.get_title());

        TextView source = currentItemView.findViewById(R.id.news_source);
        source.setText(currentNumberPosition.get_source_full_sentence());

        return currentItemView;
    }
}
