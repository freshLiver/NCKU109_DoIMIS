package com.example.finalproject;

import android.gesture.GestureUtils;
import android.widget.Toast;

import org.jsoup.HttpStatusException;
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

    protected boolean skip_flag = false;

    protected String keywords;


    protected static final String[] TVBSIllegalURLs = {
            "https://news.tvbs.com.tw/local",
            "https://news.tvbs.com.tw/world",
            "https://news.tvbs.com.tw/politics",
            "https://news.tvbs.com.tw/money",
            "https://news.tvbs.com.tw/sports"
    };

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
//        this.resActivity.insertNewsItem(new String[]{"TITLE", "CONTENT", "LINK", "MEDIA"});

        // 根據 type 以及 media 決定 google 搜尋的關鍵字然後進行搜尋、更新結果
        for (int iType = 0; iType < numTypes; ++iType)
            for (int iMedia = 0; iMedia < numMedia; ++iMedia) {
                this.resActivity.showToast("正在搜尋，請稍候");

                // get target type and media name
                String type = this.targetTypes.get(iType).replace("\n", "");
                String media = this.targetMedia.get(iMedia);

                // do search
                String searchURL = getSearchURLByTypeAndMedia(type, media);

                // get search results
                ArrayList<String[]> searchResults = getSearchResults(searchURL, media);

                // insert results into RecyclerView
                for (String[] info : searchResults)
                    resActivity.insertNewsItem(info);

            }
        this.resActivity.showToast("搜尋完成，請點擊新聞");
    }


    /***********************************************************************
     * Other Methods for crawling
     ***********************************************************************/

    /**
     * build search url by type and media name and keywords
     *
     * @param type  target type
     * @param media target media name
     * @return google search url
     */
    protected String getSearchURLByTypeAndMedia(String type, String media) {
        String url = GoogleSearchBaseKeyword + "+" + this.keywords + "+";
        switch (media) {
            case "自由時報":
                if (type.equals("社會")) {
                    url += "site:https://news.ltn.com.tw/news/society";
                } else if (type.equals("國際")) {
                    url += "site:https://news.ltn.com.tw/news/world";
                } else if (type.equals("政治")) {
                    url += "site:https://news.ltn.com.tw/news/politics";
                } else if (type.equals("財經")) {
                    url += "site:https://ec.ltn.com.tw/article";
                } else if (type.equals("體育")) {
                    url += "site:https://sports.ltn.com.tw/news/breakingnews";
                }
                break;
            case "TVBS新聞網":
                url += "site:https://news.tvbs.com.tw/";
                if (type.equals("社會")) {
                    url += "local";
                } else if (type.equals("國際")) {
                    url += "world";
                } else if (type.equals("政治")) {
                    url += "politics";
                } else if (type.equals("財經")) {
                    url += "money";
                } else if (type.equals("體育")) {
                    url += "sports";
                }
                break;
            case "中央社新聞":
                url += "site:https://www.cna.com.tw/";
                if (type.equals("社會")) {
                    url += "news/asoc";
                } else if (type.equals("國際")) {
                    url += "news/aopl";
                } else if (type.equals("政治")) {
                    url += "news/aipl";
                } else if (type.equals("財經")) {
                    url += "news/afe";
                } else if (type.equals("體育")) {
                    url += "news/aspt";
                }
                break;
            case "蘋果日報":
                url += "site:https://tw.appledaily.com/";
                if (type.equals("社會")) {
                    url += "local";
                } else if (type.equals("國際")) {
                    url += "international";
                } else if (type.equals("政治")) {
                    url += "politics";
                } else if (type.equals("財經")) {
                    url += "property";
                } else if (type.equals("體育")) {
                    url += "sports";
                }
                break;
        }
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

        // TODO search next pages

        try {
            doc_cna = Jsoup.connect(targetURL).get();
        } catch (HttpStatusException ne) {
            // check if banned
            this.resActivity.showToast("HttpStatusException (流量異常)");
            return new ArrayList<>();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // if status 200
        Elements items = doc_cna.getElementsByClass("rc");

        for (Element item : items) {
            String title, intro, link;

            // get link
            Element cls = item.getElementsByClass("yuRUbf").first();
            Element tag = cls.getElementsByTag("a").first();
            link = tag.attr("href");

            // check link
            boolean skip = false;
            for (String illegal : TVBSIllegalURLs)
                if (illegal.equals(link))
                    skip = true;

            if (skip == false) {
                // get title
                title = item.getElementsByClass("LC20lb DKV0Md").first().text();

                // get intro
                intro = item.getElementsByClass("IsZvec").first().text();


                // add item
                URL_List.add(link);
                title_List.add(title);
                text_List.add(intro);
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
