package com.example.chan1cyrus2.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.chan1cyrus2.popularmovies.data.MovieColumns;
import com.example.chan1cyrus2.popularmovies.data.MovieProvider;

/**
 * Created by chan1cyrus2 on 11/3/2015.
 */ //Task to insert movie data to Database
class SaveFavoriteMovieTask extends AsyncTask<Movie, Void, Void> {
    private final String LOG_TAG = SaveFavoriteMovieTask.class.getSimpleName();
    Context mContext;

    public SaveFavoriteMovieTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Movie... params) {
        if (params.length == 0) return null;

        Movie movie = params[0];

        ContentValues cv = new ContentValues();
        cv.put(MovieColumns.MOVIE_ID, movie.iD);
        cv.put(MovieColumns.TITLE, movie.title);
        cv.put(MovieColumns.IMGURL, movie.imgURL);
        cv.put(MovieColumns.PLOT, movie.plot);
        cv.put(MovieColumns.RATING, movie.rating);
        cv.put(MovieColumns.RELEASE_DATE, movie.release_date);

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieProvider.Movies.withId(movie.iD),
                new String[]{MovieColumns.MOVIE_ID},
                null,
                null,
                null
        );

        if (movieCursor.moveToFirst()) {
            mContext.getContentResolver().update(
                    MovieProvider.Movies.withId(movie.iD), cv, null, null);
        } else {
            mContext.getContentResolver().insert(
                    MovieProvider.Movies.CONTENT_URI, cv);
        }
        return null;
    }
}
