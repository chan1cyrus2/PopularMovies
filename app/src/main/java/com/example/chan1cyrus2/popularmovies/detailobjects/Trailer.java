package com.example.chan1cyrus2.popularmovies.detailobjects;

public class Trailer implements DetailItem {
    public final static int VIEWTYPE = 1;
    public String url;
    public String name;

    public Trailer(String url, String name){
        this.url = url;
        this.name = name;
    }

    @Override
    public int getViewType() {
        return VIEWTYPE;
    }
}
