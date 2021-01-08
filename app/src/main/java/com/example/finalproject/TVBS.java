package com.example.finalproject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TVBS extends News {
    public TVBS(String url) {
        this.url = url;
        media = "TVBS";
    }

    public void Parse_Website(String URL) {
        try {
            // send get request and parse as html
            final Document parsed = Jsoup.connect(URL).get();

            //get title
            final Element mainContent = parsed.getElementsByClass("newsdetail_box").first();
            final Element News_title = mainContent.getElementsByTag("h1").first();
            title = News_title.text();

            //get date
            final Element ArticleInfos = parsed.getElementsByClass("author").first();
            date = ArticleInfos.text();
            int cut_date_head = date.indexOf("20");
            int cut_date_tail = date.indexOf("|");
            try {
                date = date.substring(cut_date_head, cut_date_tail);
            } catch (StringIndexOutOfBoundsException e) {
                date = date.substring(cut_date_head);
            }

            //get reporter
            final Element reporters = ArticleInfos.getElementsByTag("a").first();
            try {
                reporter = reporters.text();
            } catch (NullPointerException e) {
                reporter = "無記者";
            }

            //get content
            Elements contents = mainContent.getElementsByClass("article_content");
            int get_rid_of_tail_1 = contents.text().indexOf("《TVBS》提醒您");
            int get_rid_of_tail_2 = contents.text().indexOf("～開啟小鈴鐺");
            content = contents.text();
            if (get_rid_of_tail_1 != -1) {
                content = content.substring(0, get_rid_of_tail_1);
            }
            if (get_rid_of_tail_2 != -1) {
                content = content.substring(0, get_rid_of_tail_2);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
