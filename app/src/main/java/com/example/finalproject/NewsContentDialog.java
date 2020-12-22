package com.example.finalproject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class NewsContentDialog extends Dialog implements View.OnClickListener {

    /***********************************************************************
     * DATA AND CONSTANTS
     ***********************************************************************/
    protected Context context;
    protected String newsURL;

    protected ArrayList<String> newsContents;
    protected ArrayAdapter<String> arrayAdapter;

    protected TextView TVTitles, TVReporters, TVDatetime;
    protected ListView LVContents;

    /***********************************************************************
     * CONSTRUCTOR / onCreate / setComponents
     ***********************************************************************/
    public NewsContentDialog(@NonNull Context context, String url) {
        super(context);
        this.context = context;

        this.newsURL = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_content_dialog);

        // set components
        setComponents();

        // crawl this page
        getNewsInfo();
    }

    protected void setComponents() {
        // find components
        this.TVTitles = this.findViewById(R.id.TVNewsContentTitle);
        this.TVDatetime = this.findViewById(R.id.TVNewsContentDateTime);
        this.TVReporters = this.findViewById(R.id.TVNewsContentReporters);

        this.LVContents = this.findViewById(R.id.LVNewsContentContents);
        this.newsContents = new ArrayList<>();
        this.arrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.news_content_paragraph, this.newsContents);
        this.LVContents.setAdapter(this.arrayAdapter);
    }


    /***********************************************************************
     * class methods
     ***********************************************************************/
    @Override
    public void onClick(View v) {
        // if not click dialog, close dialog
        this.dismiss();
    }

    /***********************************************************************
     * GETTERS/SETTERS
     ***********************************************************************/
    protected void getNewsInfo() {
        try {
            // use another thread to crawl news info (title, reporter, datetime, contents)
            String[] info = new String[4];

            Thread thCrawler = new Thread(() -> {
                // TODO : call crawler and get news content info
                // info = getCrawlerResult(this.newsURL);

                // THIS IS SAMPLE
                info[0] = "這是標題測試";
                info[1] = "記者";
                info[2] = "時間日期";
                info[3] = "這是\n內文\n測試";
            });

            // start and wait this thread to crawl news info
            thCrawler.start();
            thCrawler.join();

            //  update ui
            if (info != null) {
                this.TVTitles.setText(info[0]);
                this.TVReporters.setText(info[1]);
                this.TVDatetime.setText(info[2]);

                // TODO : split content into paragraphs
                // add content into newsContents and notify item changed
                this.newsContents.add(info[3]);

                this.arrayAdapter.notifyDataSetChanged();

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
