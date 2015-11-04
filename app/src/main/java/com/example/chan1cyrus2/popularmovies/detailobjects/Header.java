package com.example.chan1cyrus2.popularmovies.detailobjects;

/**
 * Created by chan1cyrus2 on 11/3/2015.
 */
public class Header implements DetailItem{
    public final static int VIEWTYPE = 3;
    public String heading;

    public Header(String heading){
        this.heading = heading;
    }

    @Override
    public int getViewType() {
        return VIEWTYPE;
    }
}
