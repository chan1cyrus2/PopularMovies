package com.example.chan1cyrus2.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private MovieAdapter mMoviesAdapter;
    public final static String PAR_KEY = "com.example.chan1cyrus2.popularmovies.Movie";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //attach gridview with our custom MovieAdapter with empty data, data
        // will be added onStart by calling FetchMovieInfoTask thread
        mMoviesAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMoviesAdapter);

        //Set up click listener when user click on the movie poster
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movieInfo = mMoviesAdapter.getItem(i);
                Bundle bundle = new Bundle();
                bundle.putParcelable(PAR_KEY, movieInfo);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtras(bundle);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieInfo();
    }

    private void updateMovieInfo(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = sharedPref.getString(getString(R.string.pref_sort_list_key),
                getString(R.string.pref_sort_popularity));
        new FetchMovieInfoTask().execute(sorting);
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

    private class FetchMovieInfoTask extends AsyncTask<String, Void, Movie[]>{
        private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();
        @Override
        protected Movie[] doInBackground(String... params) {
            //check there is input
            if (params.length == 0) return null;

            HttpURLConnection urlConnection;
            InputStream is = null;
            String moviesJsonStr = null;
            Movie[] moviesData = null;


            try{

                //Build the api request url
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String APPID_PARAM = "api_key";
                final String SORT_PARAM = "sort_by";
                final String COUNT_PARAM = "vote_count.gte";
                //strict to movies with 1000 votes to avoid showing random movies with single
                //rating of 10 when sort by vote_average
                final String COUNT_VALUE = "1000";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(APPID_PARAM, getString(R.string.movie_api_key))
                        .appendQueryParameter(COUNT_PARAM, COUNT_VALUE)
                        .build();
                URL url = new URL(builtUri.toString());
                //Log.v(LOG_TAG, "pass in " + params[0] +  " Built URI " + builtUri.toString());

                //Open a connection to the API
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                is = urlConnection.getInputStream();

                //Convert InputStream into Movie JSON String
                moviesJsonStr = readInputStream(is);

            }catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally{
                if (is!= null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            //now convert JSON String to json then to Movie object
            try {
                moviesData = getMovieDataFromJson(moviesJsonStr);
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return moviesData;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if(movies!= null){
                mMoviesAdapter.clear();
                for(Movie s:movies){
                    mMoviesAdapter.add(s);
                }
            }
        }

        private String readInputStream(InputStream stream) throws IOException{
            if (stream == null) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String MoviesJsonStr = null;

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            // Stream was empty.  No point in parsing.
            if (buffer.length() == 0) return null;

            MoviesJsonStr = buffer.toString();
            //Log.v(LOG_TAG, "Movie JSON String: " + MoviesJsonStr);

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            return MoviesJsonStr;
        }

        private Movie[] getMovieDataFromJson (String MovieJsonStr) throws JSONException{

            //JSON objects that needed to extracted
            final String JSON_RESULTS = "results";
            final String JSON_TITLE = "original_title";
            final String JSON_IMGURL = "poster_path";
            final String JSON_PLOT = "overview";
            final String JSON_RATING = "vote_average";
            final String JSON_DATE = "release_date";

            //URI construction for imgurl
            final String IMG_BASE_URL = "http://image.tmdb.org/t/p/";
            final String IMG_SIZE = "w185";


            JSONObject pageJson = new JSONObject(MovieJsonStr);
            JSONArray movieArray = pageJson.getJSONArray(JSON_RESULTS);

            Movie[] moviesData = new Movie[movieArray.length()];
            for(int i=0; i < movieArray.length(); i++){
                JSONObject movie = movieArray.getJSONObject(i);

                String title = movie.getString(JSON_TITLE);
                String plot = movie.getString(JSON_PLOT);
                double rating = movie.getDouble(JSON_RATING);
                String release_date = movie.getString(JSON_DATE);

                //Construct the full imgURL link from relative link
                Uri builtImgURi = Uri.parse(IMG_BASE_URL).buildUpon()
                        .appendEncodedPath(IMG_SIZE)
                        .appendEncodedPath(movie.getString(JSON_IMGURL))
                        .build();
                String imgURL = builtImgURi.toString();
                moviesData[i] = new Movie(title, imgURL, plot, rating, release_date);
            }
            /*for (Movie s : moviesData) {
                Log.v(LOG_TAG, "Movie entry: " + s.toString());
            }*/
            return moviesData;
        }
    }
}
