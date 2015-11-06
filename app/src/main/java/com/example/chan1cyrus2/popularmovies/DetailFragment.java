package com.example.chan1cyrus2.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.chan1cyrus2.popularmovies.detailobjects.DetailItem;
import com.example.chan1cyrus2.popularmovies.detailobjects.Header;
import com.example.chan1cyrus2.popularmovies.detailobjects.Review;
import com.example.chan1cyrus2.popularmovies.detailobjects.Trailer;

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
public class DetailFragment extends Fragment {
    @Bind(R.id.listview_detail) ListView listView;

    Movie mMovie;
    DetailAdapter mDetailAdapter;
    private ShareActionProvider mShareActionProvider;
    private String mShareTrailer;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        mDetailAdapter = new DetailAdapter(getActivity(), new ArrayList<DetailItem>());
        listView.setAdapter(mDetailAdapter);

        //Created through Two Panel mode by clicking the list from master Fragment
        // and replacing new fragment with new movie details
        if(savedInstanceState == null) {
            Bundle args = getArguments();
            if (args != null) {
                Movie movie = args.getParcelable(Movie.PAR_KEY);
                mMovie = movie;
            }

            //Created through Single Panel mode by clicking the list from master activity
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Movie.PAR_KEY)) {
                Movie movie = intent.getParcelableExtra(Movie.PAR_KEY);
                mMovie = movie;
            }
        }else{
            mMovie = savedInstanceState.getParcelable(Movie.PAR_KEY);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mMovie!= null) {
            new FetchTrailerReview().execute(mMovie.iD);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detailfragment, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareTrailer != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                Uri.parse("https://www.youtube.com/watch?").buildUpon()
                .appendQueryParameter("v", mShareTrailer).build().toString());
        return shareIntent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Movie.PAR_KEY, mMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //Task to fetch trailer and review
    private class FetchTrailerReview extends AsyncTask<String, Void, DetailItem[]>{
        private final String LOG_TAG = FetchTrailerReview.class.getSimpleName();

        @Override
        protected DetailItem[] doInBackground(String... params) {
            if (params.length == 0) return null;

            DetailItem[] items;
            String movieID = params[0];
            Trailer[] trailers = fetchTrailer(movieID);
            Review[] reviews = fetchReview(movieID);
            int trailer_length; //+1 for header if trailer is not empty
            int review_length; // +1 for header if review is not empty

            if (trailers == null) trailer_length = 0;
            else trailer_length = trailers.length+1;
            if (reviews == null) review_length = 0;
            else review_length = reviews.length+1;
            //iterate all the DetailItems (trailers, reviews) and return the combined array
            items = new DetailItem[1 + trailer_length + review_length];
            items[0] = mMovie;
            int i = 1;

            if(trailer_length != 0) {
                //update first trailer for share
                mShareTrailer = trailers[0].url;
                items[1] = new Header("Trailers:");
                i++;
                for (int j = 0; j < trailer_length - 1; j++) {
                    if (i > items.length) break;
                    items[i] = trailers[j];
                    i++;
                }
            }

            if(review_length != 0) {
                items[i] = new Header("Reviews:");
                i++;
                for (int j = 0; j < review_length - 1; j++) {
                    if (i >= items.length) break;
                    items[i] = reviews[j];
                    i++;
                }
            }
            return items;
        }

        @Override
        protected void onPostExecute(DetailItem[] detailItems) {
            if(detailItems!= null){
                ((DetailAdapter)mDetailAdapter).clear();
                for(DetailItem s:detailItems){
                    ((DetailAdapter)mDetailAdapter).add(s);
                }
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareTrailerIntent());
                }
            }
        }

        private Trailer[] fetchTrailer(String movieID){
            HttpURLConnection urlConnection = null;
            InputStream is = null;
            String jsonStr = null;
            Trailer[] trailers = null;

            try{

                //Build the api request url
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";
                final String VIDEO_PATH = "videos";

                if (isAdded()){
                    Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendPath(movieID)
                            .appendPath(VIDEO_PATH)
                            .appendQueryParameter(APPID_PARAM, getString(R.string.movie_api_key))
                            .build();
                    URL url = new URL(builtUri.toString());
                    Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                    //Open a connection to the API
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    is = urlConnection.getInputStream();

                    //Convert InputStream into Movie JSON String
                    jsonStr = Utility.readInputStream(is);
                    //Log.v(LOG_TAG, "Trailer JSON: " + jsonStr.toString());
                }else{
                    return null;
                }

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

            //now convert JSON String to json then to object
            try {
                trailers = getTrailerFromJson(jsonStr);
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return trailers;
        }

        private Trailer[] getTrailerFromJson(String jsonStr) throws JSONException{
            //JSON objects that needed to extract
            final String JSON_RESULT = "results";
            final String JSON_KEY = "key";

            //URI construction for youtubeurl
            //final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?";
            //final String VIDEO_PARAM = "v";

            JSONObject mainJson = new JSONObject(jsonStr);
            JSONArray trailerArray = mainJson.getJSONArray(JSON_RESULT);

            Trailer[] trailers = new Trailer[trailerArray.length()];
            for(int i = 0; i <trailerArray.length(); i++){
                JSONObject trailer = trailerArray.getJSONObject(i);
                String key = trailer.getString(JSON_KEY);

                //Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                        //.appendQueryParameter(VIDEO_PARAM, key)
                        //.build();
                trailers[i] = new Trailer(key, "Trailer " + Integer.toString(i+1));
                //Log.v(LOG_TAG, trailers[i].url);
            }

            return trailers;
        }

        private Review[] fetchReview(String movieID){
            HttpURLConnection urlConnection = null;
            InputStream is = null;
            String jsonStr = null;
            Review[] reviews = null;

            try{

                //Build the api request url
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";
                final String REVIEW_PATH = "reviews";

                if (isAdded()) {
                    Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendPath(movieID)
                            .appendPath(REVIEW_PATH)
                            .appendQueryParameter(APPID_PARAM, getString(R.string.movie_api_key))
                            .build();
                    URL url = new URL(builtUri.toString());
                    Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                    //Open a connection to the API
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    is = urlConnection.getInputStream();

                    //Convert InputStream into Movie JSON String
                    jsonStr = Utility.readInputStream(is);
                    //Log.v(LOG_TAG, "Trailer JSON: " + jsonStr.toString());
                }else{
                    return null;
                }

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

            //now convert JSON String to json then to object
            try {
                reviews = getReviewFromJson(jsonStr);
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return reviews;
        }

        private Review[] getReviewFromJson(String jsonStr) throws JSONException{
            //JSON objects that needed to extract
            final String JSON_RESULT = "results";
            final String JSON_CONTENT = "content";
            final String JSON_AUTHOR = "author";

            JSONObject mainJson = new JSONObject(jsonStr);
            JSONArray reviewArray = mainJson.getJSONArray(JSON_RESULT);

            Review[] reviews = new Review[reviewArray.length()];
            for(int i = 0; i <reviewArray.length(); i++){
                JSONObject trailer = reviewArray.getJSONObject(i);
                String content = trailer.getString(JSON_CONTENT);
                String author = trailer.getString(JSON_AUTHOR);
                reviews[i] = new Review(content, author);

                //Log.v(LOG_TAG, reviews[i].author);
            }

            return reviews;
        }
    }

}
