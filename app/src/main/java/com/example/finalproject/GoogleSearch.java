package com.example.finalproject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class google_new implements Runnable {

	public ArrayList<String[]> searchResult;

	//public static void main(String[] args) throws IOException, InterruptedException{
	public ArrayList<String[]> google_result(){
		ArrayList<String> URL_List = new ArrayList<String>();
		ArrayList<String> title_List = new ArrayList<String>();
		ArrayList<String> text_List = new ArrayList<String>();
		this.searchResult = new ArrayList<String[]>();
		
		Document doc_cna = null;
		try {
			doc_cna = Jsoup.connect("https://www.google.com/search?q=site:https://www.cna.com.tw/news/aopl+after:2019-06-04+before:2019-07-14").get();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String next = "";
		while(true) {
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
			if(link.size() ==10) {
				next ="https://www.google.com" + next;
				try {
					doc_cna = Jsoup.connect(next).get();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				break;
			}
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for(int i=0;i<URL_List.size();i++) {
			String[] arr = new String[3];			
			arr[0] = title_List.get(i);
			arr[1] = text_List.get(i);
			arr[2] = URL_List.get(i);
			this.searchResult.add(arr);
		}
		return this.searchResult;
	}

	@Override
	public void run() {
		google_new crawler = new google_new();
		this.searchResult = crawler.google_result();
	}
}
