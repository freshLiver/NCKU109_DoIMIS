package com.example.finalproject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GoogleSearch implements Runnable {

    /***********************************************************************
     * Sample Constants for Testing
     ***********************************************************************/
    private static final String SampleKeywords = "site:https://www.cna.com.tw/news/aopl+after:2019-06-04+before:2019-07-14";

    /***********************************************************************
     * Class & Instance Data
     ***********************************************************************/
    protected static final String GoogleSearchBaseKeyword = "https://www.google.com/search?q=";
    protected ArrayList<String> targetTypes, targetMedia;
    protected ResultActivity resActivity;

    protected String keywords;

    /***********************************************************************
     * Constructors
     ***********************************************************************/
    public GoogleSearch(ArrayList<String> types, ArrayList<String> media, String keywords, ResultActivity main) {
        // get parent
        this.resActivity = main;

        // get keywords
        this.keywords = keywords;

        // get these info and save to instance data for crawler
        this.targetTypes = types;
        this.targetMedia = media;
    }

    /***********************************************************************
     * 爬蟲 thread, android 必須建立 thread 進行網路操作, 不可用 main thread 進行
     ***********************************************************************/
    @Override
    public void run() {
        // 根據 instance data 的 types 以及 media 建立對應數量的關鍵字
        int numTypes = this.targetTypes.size();
        int numMedia = this.targetMedia.size();

        // THIS IS FOR TESTING
        resActivity.insertNewsItem(new String[]{"TITLE", "CONTENT", "link", "CNA"});

        // 根據 type 以及 media 決定 google 搜尋的關鍵字然後進行搜尋、更新結果
        for (int iType = 0; iType < numTypes; ++iType)
            for (int iMedia = 0; iMedia < numMedia; ++iMedia) {
                // get target type and media name
                String type = this.targetTypes.get(iType);
                String media = this.targetMedia.get(iMedia);

                // do search
                String searchURL = getSearchURLByTypeAndMedia(type, media);

                // get search results
                ArrayList<String[]> searchResults = getSearchResults(searchURL, media);

                // insert results into RecyclerView
                for (String[] info : searchResults) {
                    resActivity.insertNewsItem(info);
                }
            }
    }


    /***********************************************************************
     * Other Methods for crawling
     ***********************************************************************/

    /**
     * TODO, choose url by type and media name and keywords
     *
     * @param type  target type
     * @param media target media name
     * @return google search url
     */
    protected String getSearchURLByTypeAndMedia(String type, String media) {
        String url = GoogleSearchBaseKeyword + SampleKeywords;

        // TODO

        return url;
    }

    /**
     * get news search results from google search
     *
     * @param targetURL google search url
     * @param mediaName corresponding media name of this search url
     * @return list of [title, intro, url, media name]
     */
    protected ArrayList<String[]> getSearchResults(String targetURL, String mediaName) {

        ArrayList<String[]> results = new ArrayList<>();

        ArrayList<String> URL_List = new ArrayList<>();
        ArrayList<String> title_List = new ArrayList<>();
        ArrayList<String> text_List = new ArrayList<>();

        Document doc_cna = null;
        try {
            doc_cna = Jsoup.connect(targetURL).get();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String next = "";
        while (true) {
            Elements link = doc_cna.select("div.yuRUbf > a");
            Elements title = doc_cna.select("h3.LC20lb.DKV0Md");
            Elements text = doc_cna.select("span.aCOpRe");
            for (Element ele : link) {
                String url = ele.attr("href");
                URL_List.add(url);
            }

            for (Element ele : title) {
                String url = ele.text();
                title_List.add(url);
            }
            for (Element ele : text) {
                String url = ele.text();
                text_List.add(url);
            }
            Elements page = doc_cna.select("td.d6cvqb > a");
            for (Element ele : page) {
                next = ele.attr("href");
            }
            if (link.size() == 10) {
                next = "https://www.google.com" + next;
                try {
                    doc_cna = Jsoup.connect(next).get();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < URL_List.size(); i++) {
            String[] arr = new String[4];
            arr[0] = title_List.get(i);
            arr[1] = text_List.get(i);
            arr[2] = URL_List.get(i);
            arr[3] = mediaName;
            results.add(arr);
        }
        return results;
    }
}
