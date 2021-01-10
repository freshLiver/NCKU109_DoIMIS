package com.example.finalproject;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;


public class NewsContentDialog extends Dialog implements View.OnClickListener {

    /***********************************************************************
     * DATA AND CONSTANTS
     ***********************************************************************/
    protected Context context;
    protected String newsMedia, newsURL;

    protected ArrayList<String> contentParagraphs;
    protected ArrayAdapter<String> arrayAdapter;

    protected TextView TVTitles, TVReporters, TVDatetime, TVMedia;
    protected ListView LVContents;

    private MediaPlayer mediaPlayer;
    private TaiwaneseSynthesis taiwaneseSynthesis;

    private String wav_path;


    /***********************************************************************
     * CONSTRUCTOR / onCreate / setComponents
     ***********************************************************************/
    public NewsContentDialog(@NonNull Context context, String media, String url) {
        super(context);
        this.context = context;

        this.newsMedia = media;
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
        this.TVMedia = this.findViewById(R.id.TVNewsContentMedia);
        this.LVContents = this.findViewById(R.id.LVNewsContentContents);

        // set list view adapter and style
        this.contentParagraphs = new ArrayList<>();
        this.arrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.news_content_paragraph, this.contentParagraphs);
        this.LVContents.setAdapter(this.arrayAdapter);

        // set list view item click event
        this.LVContents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long l) {
                // get corresponding paragraph with position
                String paragraph = contentParagraphs.get(pos);

                // speak this paragraph in taiwanese
                Thread thPlay = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // convert to taiwanese and play this paragraph in taiwanese
                        taiwaneseSynthesis = new TaiwaneseSynthesis();
                        try {

                            wav_path = taiwaneseSynthesis.execute(paragraph).get();
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(wav_path);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.release();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                thPlay.start();
            }
        });
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
            String[] info = null;

            // call crawler and get news content info
            News crawler;
            switch (this.newsMedia) {
                case "蘋果日報":
                    crawler = new Appledaily(this.newsURL);
                    break;
                case "自由時報":
                    crawler = new LTN(this.newsURL);
                    break;
                case "中央社新聞":
                    crawler = new CNA(this.newsURL);
                    break;
                case "TVBS新聞網":
                    crawler = new TVBS(this.newsURL);
                    break;
                default:
                    throw new IllegalStateException("Unknown News Media: " + this.newsMedia);
            }

            Thread th = new Thread(crawler);
            th.start();
            th.join();

            //  update ui
            info = crawler.getArticleInfo();
            if (info != null) {

                this.TVTitles.setText(info[0]);
                this.TVMedia.setText(info[1]);
                this.TVReporters.setText(info[2]);
                this.TVDatetime.setText(info[3]);

                // split content into paragraphs
                String[] paragraphs = info[4].replace("。", "。\n").split("\n");

                // add content into newsContents and notify item changed
                this.contentParagraphs.addAll(Arrays.asList(paragraphs));
                this.arrayAdapter.notifyDataSetChanged();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
