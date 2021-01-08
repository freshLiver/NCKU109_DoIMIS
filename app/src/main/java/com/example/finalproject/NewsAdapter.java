package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    /***********************************************************************
     * an interface for item click event
     ***********************************************************************/
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListen(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /***********************************************************************
     * the inner class for building item view
     ***********************************************************************/
    protected static class NewsHolder extends RecyclerView.ViewHolder {

        // holder data
        TextView TVTitle, TVIntro, TVMedia;

        public NewsHolder(@NonNull View newsItem, OnItemClickListener listener) {
            super(newsItem);
            // get components
            TVTitle = newsItem.findViewById(R.id.TVNewsItemTitle);
            TVIntro = newsItem.findViewById(R.id.TVNewsItemIntro);
            TVMedia = newsItem.findViewById(R.id.TVNewsItemMedia);

            // 設定 newsItem 的 click listener
            newsItem.setOnClickListener(view -> {
                // 從 adapter 傳來的 listener 必須為 not null 且此 newsItem 必須不為 NO_POSITION
                int position = this.getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    // 如果 adapter 中有用 setOnItemClickListener 設定 adapter listener = listener 的話
                    listener.onItemClick(view, position);
                }
            });
        }
    }

    /***********************************************************************
     * DATA AND CONSTANTS
     ***********************************************************************/
    protected OnItemClickListener onItemClickListener;

    protected final Context context;
    protected ArrayList<String> titles, intros, media, links;

    /***********************************************************************
     * Constructors/onCreate & get bundle & component settings
     ***********************************************************************/
    public NewsAdapter(Context context) {
        // get context for holder's OnCreate event
        this.context = context;

        // init array list for my data
        this.titles = new ArrayList<>();
        this.intros = new ArrayList<>();
        this.links = new ArrayList<>();
        this.media = new ArrayList<>();

        // 在 item clicked 時就從 holder 將 position 傳給這邊執行 onItemClick
        this.setOnItemClickListen(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // get clicked item url
                String src = media.get(position);
                String link = links.get(position);


                // init a dialog for showing news contents
                NewsContentDialog contentDialog = new NewsContentDialog(v.getContext(), src, link);
                contentDialog.show();

                // get view width and height
                int width = (int) (v.getContext().getResources().getDisplayMetrics().widthPixels * 0.90);
                int height = (int) (v.getContext().getResources().getDisplayMetrics().heightPixels * 0.80);

                // get dialog window
                Window dialogWindow = contentDialog.getWindow();

                // set width and height of dialog at runtime (must set after dialog.show)
                dialogWindow.setLayout(width, height);
                dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
            }
        });
    }


    /***********************************************************************
     * Class Methods
     ***********************************************************************/
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
        View newsItem = inflater.inflate(R.layout.news_item_layout, parent, false);

        return new NewsHolder(newsItem, this.onItemClickListener);
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

    /***********************************************************************
     * Getters & Setters
     ***********************************************************************/
    @Override
    public int getItemCount() {
        return this.titles.size();
    }
}
