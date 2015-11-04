package com.example.chan1cyrus2.popularmovies;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by chan1cyrus2 on 11/2/2015.
 */
public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();
    public static String readInputStream(InputStream stream) throws IOException {
        if (stream == null) return null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String jsonStr = null;

        String line;
        while ((line = reader.readLine()) != null) {
            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
            // But it does make debugging a *lot* easier if you print out the completed
            // buffer for debugging.
            buffer.append(line + "\n");
        }
        // Stream was empty.  No point in parsing.
        if (buffer.length() == 0) return null;

        jsonStr = buffer.toString();
        //Log.v(LOG_TAG, "JSON String: " + MoviesJsonStr);

        if (reader != null) {
            try {
                reader.close();
            } catch (final IOException e) {
                Log.e(LOG_TAG, "Error closing stream", e);
            }
        }
        return jsonStr;
    }
}
