package com.example.finalproject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    Context context;
    ArrayList<String> titles, intros, media, links;


    public NewsAdapter(Context context) {
        // get context for holder's OnCreate event
        this.context = context;

        // init array list for my data
        this.titles = new ArrayList<>();
        this.intros = new ArrayList<>();
        this.links = new ArrayList<>();
        this.media = new ArrayList<>();
    }


    public void insertNewsItem(String[] item) {
        String title = item[0];
        String intro = item[1];
        String link = item[2];
        String media = item[3];

        // add to data list
        this.titles.add(title);
        this.intros.add(intro);
        this.links.add(link);
        this.media.add(media);

        // insert to here
        this.notifyItemInserted(this.titles.size() - 1);
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // a inflater to use target view as item view
        LayoutInflater inflater = LayoutInflater.from(context);

        // create a new view for this item
        View newsItemView = inflater.inflate(R.layout.news_item_layout, parent, false);

        // set onclick listener to this view
        newsItemView.setOnClickListener(v -> {
            // TODO convert to another activity and show content and news infos
            NewsContentDialog contentDialog = new NewsContentDialog(v.getContext());

            /// set this dialog at runtime
            Window window = contentDialog.getWindow();

            // set width
            int width = (int) (v.getContext().getResources().getDisplayMetrics().widthPixels * 0.90);
            int height = (int) (v.getContext().getResources().getDisplayMetrics().heightPixels * 0.70);
            contentDialog.show();
            window.setLayout(width, height);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        });

        return new NewsHolder(newsItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
        String title = this.titles.get(position);
        String intro = this.intros.get(position);
        String media = this.media.get(position);

        holder.TVTitle.setText(title);
        holder.TVIntro.setText(intro);
        holder.TVMedia.setText(media);
    }


    @Override
    public int getItemCount() {
        return this.titles.size();
    }

    /**
     * the inner class for building item view
     */
    class NewsHolder extends RecyclerView.ViewHolder {

        // holder data
        TextView TVTitle, TVIntro, TVMedia;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);
            TVTitle = itemView.findViewById(R.id.TVNewsItemTitle);
            TVIntro = itemView.findViewById(R.id.TVNewsItemIntro);
            TVMedia = itemView.findViewById(R.id.TVNewsItemMedia);
        }
    }
}
