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
            final Element articleAndPhotos = mainContent.getElementsByClass("text boxTitle boxText").first();
            final StringBuilder contents = new StringBuilder();
            for (Element child : articleAndPhotos.children())
                if ("p".equals(child.tagName()) == true && "".equals(child.className()) == true)
                    contents.append(child.text());
            content = contents.toString().substring(contents.toString().indexOf("〕") + 1);

            //get reporter
            final String reporters = contents.substring(contents.toString().indexOf("〔"), contents.toString().indexOf("〕") + 1);
            reporter = reporters;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
