package com.filbert.online_guard;

public class NewsView {

    private String title;
    private String url;
    private String source;

    // Constructor
    public NewsView(String t, String u, String s){
        title = t;
        url = u;
        source = s;
    }

    public String get_title(){
        return title;
    }

    public String get_url(){
        return url;
    }

    public String get_source(){
        return source;
    }

    public String get_source_full_sentence(){
        return "Source: " + source;
    }
}
