package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private ArrayList<String[]> searchResults;
    private RecyclerView RVSearchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        // set button listener
        Button BtnResultBackToOpt = findViewById(R.id.BtnResultBackToOpt);
        BtnResultBackToOpt.setOnClickListener(new ResultButtonMapping(this));

        // SAMPLE add some data for testing recycle view
        this.searchResults = new ArrayList<>();

        this.searchResults.add(new String[]{
                "植物肉浪潮／植物肉釣出愛嘗鮮族群超商餐飲業搶食600億商機 ...",
                "歐美掀起綠色飲食風潮，台灣餐飲、零售業者看好商機，陸續推出植物肉食品，",
                "link",
                "中央社 CNA"
        });

//        Thread result = new Thread(new GoogleSearch());
//        result.start();

        // get my recycle view
        RVSearchResults = findViewById(R.id.RVSearchResults);

        // instantiate my adapter and set it for recycle view
        NewsAdapter newsAdapter = new NewsAdapter(this, this.searchResults);
        RVSearchResults.setAdapter(newsAdapter);
        RVSearchResults.setLayoutManager(new LinearLayoutManager(this));
    }

    public void setSearchResults(ArrayList<String[]> results) {
        for (String[] result : results) {
            this.insertNewsItem(result);
        }
    }

    public int getResultsSize() {
        return this.searchResults.size();
    }

    public void insertNewsItem(String[] newsItem) {

        NewsAdapter adp = (NewsAdapter) this.RVSearchResults.getAdapter();
        if (adp != null) {
            adp.insertNewsItem(newsItem);
        }
    }

}

class ResultButtonMapping implements View.OnClickListener {

    private final ResultActivity src;
    private RecyclerView rv;

    public ResultButtonMapping(ResultActivity main) {
        this.src = main;
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BtnResultBackToOpt:

                // sample data to insert a news item
                String[] newsItem = new String[]{
                        "News Title ",
                        "this is a new intro",
                        "link",
                        "Media Name"
                };
                // insert item into recyclerview
                this.src.insertNewsItem(newsItem);

                break;
            default:
                break;
        }
    }
}