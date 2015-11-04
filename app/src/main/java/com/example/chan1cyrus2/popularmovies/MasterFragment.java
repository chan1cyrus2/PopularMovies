package com.example.chan1cyrus2.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;

import com.example.chan1cyrus2.popularmovies.data.MovieColumns;
import com.example.chan1cyrus2.popularmovies.data.MovieProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MasterFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    @Bind(R.id.gridview_movies) GridView gridView;

    private final String LOG_TAG = MasterFragment.class.getSimpleName();
    public static final int MOVIE_FAV_LOADER = 0;

    private MovieArrayAdapter mMoviesArrayAdapter; //fetch data from API
    private MovieCursorAdapter mMoviesCursorAdapter; //fetch data from database
    private boolean mFavorite;

    //TODO: update mPosition

    public MasterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_master, container, false);
        ButterKnife.bind(this, rootView);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = sharedPref.getString(getString(R.string.pref_sort_list_key),
                getString(R.string.pref_sort_popularity));
        mMoviesCursorAdapter = new MovieCursorAdapter(getActivity(), null, 0);
        mMoviesArrayAdapter = new MovieArrayAdapter(getActivity(), new ArrayList<Movie>());
        //Decide how we fetch data, from databasae if sorting is favorite
        // or from API request for others
        if (sorting.equals(getString(R.string.pref_sort_favorite))){
            mFavorite = true;
            gridView.setAdapter(mMoviesCursorAdapter);
        }else {
            mFavorite = false;
            gridView.setAdapter(mMoviesArrayAdapter);
        }

        //Set up click listener when user click on the movie poster
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //run the callback function implemented by the activity contained this fragment
                Movie movie = null;
                Object item = adapterView.getItemAtPosition(i);
                if(item instanceof Movie){
                    movie = (Movie)item;
                }else if(item instanceof Cursor){
                    Cursor cursorItem = (Cursor) item;
                    //change the cursor structure to Movie
                    movie = new Movie(
                            cursorItem.getString(cursorItem.getColumnIndex(MovieColumns.MOVIE_ID)),
                            cursorItem.getString(cursorItem.getColumnIndex(MovieColumns.TITLE)),
                            cursorItem.getString(cursorItem.getColumnIndex(MovieColumns.IMGURL)),
                            cursorItem.getString(cursorItem.getColumnIndex(MovieColumns.PLOT)),
                            Double.parseDouble(cursorItem.getString(cursorItem.getColumnIndex(MovieColumns.MOVIE_ID))),
                            cursorItem.getString(cursorItem.getColumnIndex(MovieColumns.RELEASE_DATE)));
                }
                ((Callback)getActivity()).onItemSelected(movie);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        Log.v(LOG_TAG, "onStart");
        super.onStart();
        //Also update mFavorite in onStart
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = sharedPref.getString(getString(R.string.pref_sort_list_key),
                getString(R.string.pref_sort_popularity));
        if (sorting.equals(getString(R.string.pref_sort_favorite))){
            if(mFavorite == false){
                gridView.setAdapter(mMoviesCursorAdapter);
                getLoaderManager().initLoader(MOVIE_FAV_LOADER, null, this);
            }
            mFavorite = true;
        }else {
            if(mFavorite == true) {
                gridView.setAdapter(mMoviesArrayAdapter);
            }
            mFavorite = false;
            updateMovieInfo();

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onActivityCreated");
        if(mFavorite) {
            Log.v(LOG_TAG, "Init Loader");
            getLoaderManager().initLoader(MOVIE_FAV_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //LoaderCallbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {MovieColumns.MOVIE_ID ,MovieColumns.IMGURL};
        return new CursorLoader(getActivity(),
                MovieProvider.Movies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((CursorAdapter)mMoviesCursorAdapter).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((CursorAdapter)mMoviesCursorAdapter).swapCursor(null);
    }

    private void updateMovieInfo(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = sharedPref.getString(getString(R.string.pref_sort_list_key),
                getString(R.string.pref_sort_popularity));
        //if it is on favorite setting, check database, else go api to get the rest
        new FetchMovieInfoTask().execute(sorting);
    }

    /**
     * A callback interface that all activities containing this fragment must implement. This
     * mechanism allows activities to be notified of item selection.
     */
    interface Callback{
        //When item has been selected on the listView
        public void onItemSelected(Movie movie);
    }

    private class FetchMovieInfoTask extends AsyncTask<String, Void, Movie[]>{
        private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();
        @Override
        protected Movie[] doInBackground(String... params) {
            //check there is input
            if (params.length == 0) return null;

            HttpURLConnection urlConnection = null;
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
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                //Open a connection to the API
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                is = urlConnection.getInputStream();

                //Convert InputStream into Movie JSON String
                moviesJsonStr = Utility.readInputStream(is);
                //Log.v(LOG_TAG, "Movie JSON: " + moviesJsonStr.toString());

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
                if(urlConnection != null){
                    urlConnection.disconnect();
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
                ((MovieArrayAdapter)mMoviesArrayAdapter).clear();
                for(Movie s:movies){
                    ((MovieArrayAdapter)mMoviesArrayAdapter).add(s);
                }
            }
        }

        private Movie[] getMovieDataFromJson (String MovieJsonStr) throws JSONException{

            //JSON objects that needed to extracted
            final String JSON_RESULTS = "results";
            final String JSON_ID = "id";
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

                String iD = movie.getString(JSON_ID);
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
                moviesData[i] = new Movie(iD, title, imgURL, plot, rating, release_date);
            }
            /*for (Movie s : moviesData) {
                Log.v(LOG_TAG, "Movie entry: " + s.toString());
            }*/
            return moviesData;
        }
    }
}
