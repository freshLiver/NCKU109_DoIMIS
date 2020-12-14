package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private ArrayList<String[]> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // SAMPLE add some data for testing recycle view
        searchResults = new ArrayList<>();

        searchResults.add(new String[]{
                "Title1", "Intro1", "Link1"
        });
        searchResults.add(new String[]{
                "Title2", "Intro2", "Link2"
        });
        searchResults.add(new String[]{
                "Title3", "Intro3", "Link3"
        });
        searchResults.add(new String[]{
                "Title4", "Intro4", "Link4"
        });
        searchResults.add(new String[]{
                "Title5", "Intro5", "Link5"
        });
        searchResults.add(new String[]{
                "Title6", "Intro6", "Link6"
        });
        searchResults.add(new String[]{
                "Title7", "Intro7", "Link7"
        });

        // get my recycle view
        RecyclerView RVSearchResults = findViewById(R.id.RVSearchResults);

        // instantiate my adapter and set it for recycle view
        NewsAdapter newsAdapter = new NewsAdapter(this, this.searchResults);
        RVSearchResults.setAdapter(newsAdapter);
        RVSearchResults.setLayoutManager(new LinearLayoutManager(this));
    }
}