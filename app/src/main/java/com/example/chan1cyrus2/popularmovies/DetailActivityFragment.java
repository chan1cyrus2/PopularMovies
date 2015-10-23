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

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(MainActivityFragment.PAR_KEY)) {
            Movie movie = intent.getParcelableExtra(MainActivityFragment.PAR_KEY);
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.title);
            Picasso.with(getContext()).load(movie.imgURL).into((ImageView) rootView.findViewById(R.id.movie_img));
            ((TextView) rootView.findViewById(R.id.movie_plot)).setText(movie.plot);
            ((TextView) rootView.findViewById(R.id.movie_rating)).setText("rating: " + Double.toString(movie.rating));
            ((TextView) rootView.findViewById(R.id.movie_date)).setText("release date: " + movie.release_date);

        }
        return rootView;
    }
}
