package com.example.finalproject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class LTN extends News {
    public LTN(String url) {
        this.url = url;
        media = "LTN";
    }


    public void Parse_Website(String URL) {
        //Parse.............
        try {
            // send get request and parse as html
            final Document soup = Jsoup.connect(URL).get();

            final Element mainContent = soup.getElementsByClass("whitecon").first();

            //get title            
            final Element News_title = mainContent.getElementsByTag("h1").first();
            title = News_title.text();

            //get date 
            final Element datetime = mainContent.getElementsByClass("time").first();
            date = datetime.text();

            //get content     
            Element articleAndPhotos = mainContent.getElementsByClass("text boxTitle boxText").first();

            // if articleAndPhotos is null
            if (articleAndPhotos == null)
                articleAndPhotos = mainContent.getElementsByClass("text").first();

            final StringBuilder contents = new StringBuilder();
            for (Element child : articleAndPhotos.children())
                if ("p".equals(child.tagName()) == true && "".equals(child.className()) == true)
                    contents.append(child.text());
            content = contents.toString().substring(contents.toString().indexOf("〕") + 1);

            //get reporter
            int begin = contents.toString().indexOf("〔");
            int finish = contents.toString().indexOf("〕") + 1;

            reporter = "無記者";
            if (begin != -1 && finish != -1) {;
                reporter = contents.substring(begin, finish);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
