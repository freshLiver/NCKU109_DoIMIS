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
            Document doc_apple = Jsoup.connect(URL).get();
            
            
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
                y = y.replace("<BR>", "");
                content +=y;
            }
            if(content.indexOf("《蘋果》關心你")!=-1) {
                content = content.substring(0, content.indexOf("《蘋果》關心你"));
            }
            if(content.substring(0, 1).equals("【")) {
                reporter =content.substring(0, content.indexOf("報導")+3);
                content = content.substring(content.indexOf("報導")+3, content.lastIndexOf("。") + 1);
            }else if(content.lastIndexOf("報導）")!=-1){
                reporter = content.substring(content.lastIndexOf("。") + 1, content.lastIndexOf("報導")+3);
                content = content.substring(0,content.lastIndexOf("。") + 1);
            }else {
                content = content.substring(0,content.lastIndexOf("。") + 1);
                reporter = "無記者";
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
