package com.example.finalproject;


import android.widget.Toast;

public abstract class News implements Runnable {
    protected String reporter, content, date, title, media, url;

    public abstract void Parse_Website(String URL);

    public void run() {
        System.out.println("URL = " + url);
        Parse_Website(url);
    }

    public String[] getArticleInfo() {
        return new String[]{
                this.Get_title(),
                this.Get_media(),
                this.Get_reporter(),
                this.Get_date(),
                this.Get_content(),
        };
    }

    public String Get_media() {
        return media;
    }

    public String Get_title() {
        if (title.length() != 0) {
            return title;
        }
        return "NULL";
    }

    public String Get_reporter() {
        if (reporter.length() != 0) {
            return reporter;
        }
        return "NULL";
    }

    public String Get_content() {
        if (content.length() != 0) {
            return content;
        }
        return "NULL";
    }

    public String Get_date() {
        if (date.length() != 0) {
            return date;
        }
        return "NULL";
    }
}
