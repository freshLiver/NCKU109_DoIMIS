package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    Context context;
    ArrayList<String> titles, intros, links;


    public NewsAdapter(Context context, ArrayList<String[]> searchResults) {
        // get context for holder's OnCreate event
        this.context = context;

        // init array list for my data
        this.titles = new ArrayList<>();
        this.intros = new ArrayList<>();
        this.links = new ArrayList<>();

        // split result infos into title, intro, link
        for (String[] item : searchResults) {
            this.titles.add(item[0]);
            this.intros.add(item[1]);
            this.links.add(item[2]);
        }
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View newsItemView = inflater.inflate(R.layout.news_item_layout, parent, false);
        return new NewsHolder(newsItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
        String title = this.titles.get(position);
        String intro = this.intros.get(position);

        holder.TVTitle.setText(title);
        holder.TVIntro.setText(intro);
    }

    @Override
    public int getItemCount() {
        return this.titles.size();
    }


    public class NewsHolder extends RecyclerView.ViewHolder {

        // holder data
        TextView TVTitle, TVIntro;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);
            TVTitle = itemView.findViewById(R.id.TVNewsItemTitle);
            TVIntro = itemView.findViewById(R.id.TVNewsItemIntro);
        }
    }
}
