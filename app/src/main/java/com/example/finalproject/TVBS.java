package com.example.finalproject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TVBS extends  News{
	public TVBS(String site,String type) {
        this.site = site;
        this.type = type;
        media = "TVBS";
    }
	
	
	public void Parse_Wedsite(String URL) {
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
          int  cut_date_head  = date.indexOf("發佈時間：");
          int  cut_date_tail  = date.indexOf("|");
          date = date.substring(cut_date_head+5, cut_date_tail );
          
          //get reporter
          final Element reporters = ArticleInfos.getElementsByTag("a").first();
          reporter = reporters.text();

          //get content
          Elements contents = mainContent.getElementsByClass("article_content");
          int  get_rid_of_tail_1  = contents.text().indexOf("《TVBS》提醒您");
          int  get_rid_of_tail_2  = contents.text().indexOf("～開啟小鈴鐺");
          content = contents.text();
          if(get_rid_of_tail_1!=-1) {
        	  content = content.substring(0, get_rid_of_tail_1);
          }
          if(get_rid_of_tail_2!=-1) {
        	  content = content.substring(0, get_rid_of_tail_2);
          }
          
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
	}
}
