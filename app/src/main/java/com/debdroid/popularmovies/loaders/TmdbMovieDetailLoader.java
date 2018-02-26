package com.debdroid.popularmovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.debdroid.popularmovies.MovieListActivity;
import com.debdroid.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by debashispaul on 26/02/2018.
 */


public class TmdbMovieDetailLoader extends AsyncTaskLoader<String> {

    private final Bundle mBundle;

    public TmdbMovieDetailLoader(Context context, Bundle bundle) {
        super(context);
        this.mBundle = bundle;
    }

    // Create a String member variable to store the raw JSON from TMDb endpoint
    private String mTmdbJson;

    @Override
    protected void onStartLoading() {

        // If no arguments were passed, nothing to do
        if (mBundle == null) {
            return;
        }

        // If mTmdbJson is not null, deliver that result. Otherwise, force a load
        if(mTmdbJson != null) {
            deliverResult(mTmdbJson);
        } else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        // Extract the url from the args using the key
        String tmdbQueryUrlString = mBundle.getString(MovieListActivity.TMDB_QUERY_URL_EXTRA);

        // Parse the URL from the passed in String and perform the GET request
        try {
            URL tmdbUrl = new URL(tmdbQueryUrlString);
            return NetworkUtils.getResponseFromTmdbHttpUrl(tmdbUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(String data) {
        mTmdbJson = data;
        super.deliverResult(data);
    }
}
