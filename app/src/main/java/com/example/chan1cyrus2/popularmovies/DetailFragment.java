package com.example.chan1cyrus2.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chan1cyrus2.popularmovies.data.MovieColumns;
import com.example.chan1cyrus2.popularmovies.data.MovieProvider;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
    @Bind(R.id.movie_title) TextView movieTitle;
    @Bind(R.id.movie_img) ImageView movieImg;
    @Bind(R.id.movie_plot) TextView moviePlot;
    @Bind(R.id.movie_rating) TextView movieRating;
    @Bind(R.id.movie_date) TextView movieDate;
    @Bind(R.id.favorite_button) Button favoriteButton;

    Movie mMovie;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        //Created through Two Panel mode by clicking the list from master Fragment
        // and replacing new fragment with new movie details
        Bundle args = getArguments();
        if(args != null){
            Movie movie = args.getParcelable(Movie.PAR_KEY);
            mMovie = movie;
            movieTitle.setText(movie.title);
            Picasso.with(getContext()).load(movie.imgURL).into(movieImg);
            moviePlot.setText(movie.plot);
            movieRating.setText("rating: " + Double.toString(movie.rating));
            movieDate.setText("release date: " + movie.release_date);

        }

        //Created through Single Panel mode by clicking the list from master activity
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Movie.PAR_KEY)) {
            Movie movie = intent.getParcelableExtra(Movie.PAR_KEY);
            mMovie = movie;
            movieTitle.setText(movie.title);
            Picasso.with(getContext()).load(movie.imgURL).into(movieImg);
            moviePlot.setText(movie.plot);
            movieRating.setText("rating: " + Double.toString(movie.rating));
            movieDate.setText("release date: " + movie.release_date);
        }

        favoriteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View V){
                SaveFavoriteMovieTask task = new SaveFavoriteMovieTask(getActivity());
                task.execute(mMovie);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //Task to insert movie data to Database
    private class SaveFavoriteMovieTask extends AsyncTask<Movie, Void, Void>{
        private final String LOG_TAG = SaveFavoriteMovieTask.class.getSimpleName();
        Context mContext;
        public SaveFavoriteMovieTask(Context context){
            mContext=context;
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

            if(movieCursor.moveToFirst()) {
                mContext.getContentResolver().update(
                        MovieProvider.Movies.withId(movie.iD), cv, null, null);
            }else{
                mContext.getContentResolver().insert(
                        MovieProvider.Movies.CONTENT_URI, cv);
            }
            return null;
        }
    }

    //Task to fetch trailer
    //private class FetchTrailer extends AsyncTask<String, Void, String[]>{

    //}
}
