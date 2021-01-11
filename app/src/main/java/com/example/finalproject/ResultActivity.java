package com.example.finalproject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class ResultActivity extends Activity {

    /***********************************************************************
     * DATA AND CONSTANTS
     ***********************************************************************/
    protected RecyclerView RVSearchResults;

    protected String keywords;
    protected ArrayList<String> types, media;

    protected ImageButton BtnResultBackToOpt;


    protected Context context;

    protected int fontSizeShift;

    /***********************************************************************
     * Constructors/onCreate & get bundle & component settings
     ***********************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // must before setContentView
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_result);

        this.context = this;
        this.fontSizeShift = 0;

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
        this.BtnResultBackToOpt = this.findViewById(R.id.BtnResultBackToOpt);

        // set buttons listener
        // finish while back clicked
        this.BtnResultBackToOpt.setOnClickListener(v -> finish());

        // get my recycle view
        this.RVSearchResults = findViewById(R.id.RVSearchResults);

        // instantiate my adapter and set it for recycle view
        NewsAdapter newsAdapter = new NewsAdapter(this);
        this.RVSearchResults.setAdapter(newsAdapter);
        this.RVSearchResults.setLayoutManager(new LinearLayoutManager(this));
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
    }

    public void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
    }

    /***********************************************************************
     * GETTERS/SETTERS
     ***********************************************************************/
}