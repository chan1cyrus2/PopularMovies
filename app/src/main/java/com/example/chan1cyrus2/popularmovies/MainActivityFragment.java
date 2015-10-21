package com.example.chan1cyrus2.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Movie[] data = {
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),
                new Movie("http://i.imgur.com/DvpvklR.png"),

        };


        List<Movie> dummydata = new ArrayList<>(Arrays.asList(data));

        MovieAdapter moviesAdapter = new MovieAdapter(
                getActivity(),
                dummydata
        );

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(moviesAdapter);

        return rootView;

    }

    public class MovieAdapter extends ArrayAdapter<Movie>{
        /**
         * This is our own custom constructor (it doesn't mirror a superclass constructor).
         * The context is used to inflate the layout file, and the List is the data we want
         * to populate into the lists
         *
         * @param context        The current context. Used to inflate the layout file.
         * @param movies         A List of Movie objects to display in a list
         */
        public MovieAdapter(Activity context, List<Movie> movies){
            super(context, 0, movies);
        }

        /**
         * Provides a view for an AdapterView (ListView, GridView, etc.)
         *
         * @param position    The AdapterView position that is requesting a view
         * @param convertView The recycled view to populate.
         *                    (search online for "android view recycling" to learn more)
         * @param parent The parent ViewGroup that is used for inflation.
         * @return The View for the position in the AdapterView.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
            Movie movie = getItem(position);

            // Adapters recycle views to AdapterViews.
            // If this is a new View object we're getting, then inflate the layout.
            // If not, this view already has the layout inflated from a previous call to getView,
            // and we modify the View widgets as usual.
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movies, parent, false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_movies_text_view);
            Picasso.with(getContext()).load(movie.imgURL).into(imageView);
            //imageView.setImageResource(movie.imgURL);

            return imageView;

        }
    }
}
