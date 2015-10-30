package com.example.chan1cyrus2.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    @Bind(R.id.movie_title) TextView movieTitle;
    @Bind(R.id.movie_img) ImageView movieImg;
    @Bind(R.id.movie_plot) TextView moviePlot;
    @Bind(R.id.movie_rating) TextView movieRating;
    @Bind(R.id.movie_date) TextView movieDate;


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        Intent intent = getActivity().getIntent();

        if(intent != null && intent.hasExtra(MainActivityFragment.PAR_KEY)) {
            Movie movie = intent.getParcelableExtra(MainActivityFragment.PAR_KEY);
            movieTitle.setText(movie.title);
            Picasso.with(getContext()).load(movie.imgURL).into(movieImg);
            moviePlot.setText(movie.plot);
            movieRating.setText("rating: " + Double.toString(movie.rating));
            movieDate.setText("release date: " + movie.release_date);

        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
