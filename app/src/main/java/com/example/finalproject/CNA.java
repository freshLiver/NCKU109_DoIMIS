package com.example.finalproject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class CNA extends News{
	public CNA(String site,String type) {
        this.site = site;
        this.type = type;
        media = "NewTalk";
    }
	
	
	public void Parse_Website(String URL) {
		try {

			// send get request and parse as html
			Document doc_cna = Jsoup.connect(URL).get();
						
			//get title 
			title = doc_cna.select("div.centralContent > h1").text();	
						
			//get date
			date = doc_cna.select("div.updatetime > span").text();		
			
			//get content
			Element get_content = doc_cna.select("div.paragraph").first();
			for (Element child : get_content.children())
                if ("p".equals(child.tagName()) == true && "".equals(child.className()) == true)
                    content += child.text();
			content = content.replaceAll("null", "");
			reporter = content;
			content = content.substring(content.indexOf("）")+1);
			content = content.substring(0, content.lastIndexOf("。")+1);
			
			//get reporter
			reporter = reporter.substring(0, reporter.indexOf("）")+1);
            
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
		
	}
}
