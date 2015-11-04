package com.example.chan1cyrus2.popularmovies.detailobjects;

/**
 * Created by chan1cyrus2 on 11/2/2015.
 */
public class Review implements DetailItem {
    public final static int VIEWTYPE = 2;
    public String review;
    public String author;

    public Review(String review, String author){
        this.review = review;
        this.author = author;
    }

    @Override
    public int getViewType() {
        return VIEWTYPE;
    }
}
