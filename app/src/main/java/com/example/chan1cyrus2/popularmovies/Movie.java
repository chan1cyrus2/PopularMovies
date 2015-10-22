package com.example.chan1cyrus2.popularmovies;

public class Movie {
    String title;
    String imgURL;
    String plot;
    double rating;
    String release_date;

    public Movie(String title, String imgURL, String plot, double rating, String release_date){
        this.title = title;
        this.imgURL = imgURL;
        this.plot = plot;
        this.rating = rating;
        this.release_date = release_date;
    }

    public String toString (){
        return title + "\n" +
                imgURL + "\n" +
                plot + "\n" +
                String.valueOf(rating) + "\n" +
                release_date + "\n";
    }
}
