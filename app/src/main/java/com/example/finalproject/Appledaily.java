package com.example.finalproject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Appledaily extends News {
    public Appledaily(String url) {
        this.url = url;
        media = "Appledaily";
    }


    public void Parse_Website(String URL) {
        //Parse.............

        try {
            // send get request and parse as html
            Document doc_apple = Jsoup.connect(URL).get();

            //get title
            title = doc_apple.select(".text_medium").text();


            //get date
            date = doc_apple.select(".timestamp").text();
            date = date.substring(date.indexOf("更新時間： ") + 6);


            //get content
            content = doc_apple.select("p[class~=^text]").text();
            content = content.substring(0, content.lastIndexOf("。") + 1);

            //get reporter
            reporter = doc_apple.select("p[class~=^text]").text();
            reporter = reporter.substring(reporter.lastIndexOf("。") + 1, reporter.lastIndexOf("出版"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
