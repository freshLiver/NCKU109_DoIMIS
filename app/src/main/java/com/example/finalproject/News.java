package com.example.finalproject;


public abstract class News {
	protected String reporter,content,date,title,site,media,type;
	
	public abstract void Parse_Wedsite(String URL);
				
	
	public String Get_type() {
		return type;
	}
	
	public String Get_media() {
		return media;
	}
	
	public String Get_title() {
		if(title.length()!=0) {
			return title;
		}
		return "NULL";
	}
	
	public String Get_reporter() {
		if(reporter.length()!=0) {
			return reporter;
		}
		return "NULL";
	}
	
	public String Get_content() {
		if(content.length()!=0) {
			return content;
		}
		return "NULL";
	}
	
	public String Get_date() {
		if(date.length()!=0) {
			return date;
		}
		return "NULL";
	}
}
