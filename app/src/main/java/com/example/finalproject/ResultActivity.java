package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class ResultActivity extends AppCompatActivity {

    /***********************************************************************
     * DATA AND CONSTANTS
     ***********************************************************************/
    protected RecyclerView RVSearchResults;

    protected String keywords;
    protected ArrayList<String> types, media;


    /***********************************************************************
     * Constructors/onCreate & get bundle & component settings
     ***********************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // get bundles and set components
        getBundles();
        setComponents();

        // do google search with given keywords
        Thread thSearch = new Thread(new GoogleSearch(types, media, keywords, this));
        thSearch.start();

    }

    protected void getBundles() {
        Bundle bundle = this.getIntent().getExtras();

        this.keywords = bundle.getString("KEYWORDS");
        this.types = bundle.getStringArrayList("TYPES");
        this.media = bundle.getStringArrayList("MEDIA");
    }

    protected void setComponents() {
        // set button listener
        Button BtnResultBackToOpt = findViewById(R.id.BtnResultBackToOpt);
        BtnResultBackToOpt.setOnClickListener(new ResultButtonMapping(this));

        // get my recycle view
        RVSearchResults = findViewById(R.id.RVSearchResults);

        // instantiate my adapter and set it for recycle view
        NewsAdapter newsAdapter = new NewsAdapter(this);
        RVSearchResults.setAdapter(newsAdapter);
        RVSearchResults.setLayoutManager(new LinearLayoutManager(this));
    }

    /***********************************************************************
     * CLASS METHODS
     ***********************************************************************/

    public void insertNewsItem(String[] newsItem) {
        // get adapter from RecyclerView
        NewsAdapter adp = (NewsAdapter) this.RVSearchResults.getAdapter();
        if (adp != null) {
            // only main thread can update ui
            runOnUiThread(() -> adp.insertNewsItem(newsItem));
        }

        // TODO should also insert to this.results
    }


    /***********************************************************************
     * GETTERS/SETTERS
     ***********************************************************************/


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