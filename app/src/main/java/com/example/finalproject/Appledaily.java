package com.example.finalproject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;


public class Appledaily extends News {
    public Appledaily(String url) {
        this.url = url;
        media = "Appledaily";
    }


    public void Parse_Website(String URL) {
        //Parse.............

        try {
            // send get request and parse as html
            Document doc_apple = Jsoup.connect("https://tw.appledaily.com/local/20200703/PIEAD3K4RMRV4HYBZ3LVRGYX34").get();
            
            
            //get title
            title = doc_apple.select(".text_medium").text();

            //get date
            date = doc_apple.select(".timestamp").text();
            if(date.indexOf("更新時間")!=-1) {
            	date = date.substring(date.indexOf("更新時間") + 6);
            }
            //get content
            content = doc_apple.select("p[class~=^text]").text();
            ArrayList ad = new ArrayList();
            Elements es = doc_apple.getElementsByTag("script");
            for (Element child : es) {
            	ad.add(child.toString());
            }

            String y = ad.get(17).toString();
            int x = y.indexOf("raw_html");
            if(x!=-1) {
            	int t = y.indexOf("}", x+21);
                y=y.substring(x+21, t);
                y = y.replace("<br />", "");
                y = y.replace("&nbsp;", "");
                reporter = y;
                y = y.substring(0, y.lastIndexOf("。") + 1);
                content +=y;                
            }else {
            	reporter = content;
                content = content.substring(0, content.lastIndexOf("。") + 1);
            }

            //get reporter
            reporter = reporter.substring(reporter.lastIndexOf("。") + 1, reporter.lastIndexOf("報導")+3);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
