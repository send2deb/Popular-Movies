package com.debdroid.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.debdroid.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by debashispaul on 19/02/2018.
 */

public class NetworkUtils {

    private static final String TMDB_MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String PRAM_QUERY_API_KEY = "api_key";


    /**
     * Builds the URL used to query TMDb endpoint for movie list.
     *
     * @param movieSortCategory The movie category that will be queried for.
     * @return The URL to use to query the TMDb.
     */
    public static URL buildTmdbMovieListUrl(String movieSortCategory) {
        Uri builtUri = Uri.parse(TMDB_MOVIE_BASE_URL).buildUpon()
                .appendPath(movieSortCategory)
                .appendQueryParameter(PRAM_QUERY_API_KEY, BuildConfig.TMDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response of TMDb endpoint
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading.
     */
    public static String getResponseFromTmdbHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method checks if the device is connected to a network to perform network operation.
     * @param ctx Application Context
     * @return True if WiFi or Mobile network is available otherwise returns False
     */
    public static boolean isOnline(final Context ctx) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        return activeInfo != null && activeInfo.isConnected();
    }
}
