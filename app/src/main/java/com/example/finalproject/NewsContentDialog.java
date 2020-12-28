package com.example.finalproject;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NewsContentDialog extends Dialog implements View.OnClickListener {

    /***********************************************************************
     * DATA AND CONSTANTS
     ***********************************************************************/
    protected Context context;
    protected String newsURL;

    protected ArrayList<String> contentParagraphs;
    protected ArrayAdapter<String> arrayAdapter;

    protected TextView TVTitles, TVReporters, TVDatetime;
    protected ListView LVContents;

    private MediaPlayer mediaPlayer;
    private TaiwaneseSynthesis taiwaneseSynthesis;
    private String wav_path;


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

                // TODO : speak this paragraph in taiwanese
                Toast.makeText(v.getContext(), paragraph, Toast.LENGTH_SHORT).show();
                Thread thPlay = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO : convert to taiwanese and play this paragraph in taiwanese
                        taiwaneseSynthesis= new TaiwaneseSynthesis();
                        try{

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
                        }catch(ExecutionException e){
                            e.printStackTrace();
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }catch(IOException e){
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
                this.contentParagraphs.add(info[3]);

                this.contentParagraphs.add("這是2");
                this.contentParagraphs.add("這是3");
                this.contentParagraphs.add("這是4");
                this.contentParagraphs.add("info[3]");
                this.arrayAdapter.notifyDataSetChanged();

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
