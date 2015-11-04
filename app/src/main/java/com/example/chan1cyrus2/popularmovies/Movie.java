package com.example.chan1cyrus2.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.chan1cyrus2.popularmovies.detailobjects.DetailItem;

public class Movie implements Parcelable, DetailItem {
    public final static String PAR_KEY = "com.example.chan1cyrus2.popularmovies.Movie";
    public final static int VIEWTYPE = 0;

    String iD;
    String title;
    String imgURL;
    String plot;
    double rating;
    String release_date;

    public Movie(String iD, String title, String imgURL, String plot, double rating, String release_date){
        this.iD = iD;
        this.title = title;
        this.imgURL = imgURL;
        this.plot = plot;
        this.rating = rating;
        this.release_date = release_date;
    }

    public Movie(Parcel in){
        this.iD = in.readString();
        this.title = in.readString();
        this.imgURL = in.readString();
        this.plot = in.readString();
        this.rating = in.readDouble();
        this.release_date = in.readString();
    }


    public String toString (){
        return title + "\n" +
                imgURL + "\n" +
                plot + "\n" +
                String.valueOf(rating) + "\n" +
                release_date + "\n";
    }

    //For Parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iD);
        dest.writeString(title);
        dest.writeString(imgURL);
        dest.writeString(plot);
        dest.writeDouble(rating);
        dest.writeString(release_date);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    //For Detail Item interface
    @Override
    public int getViewType() {
        return VIEWTYPE;
    }
}
