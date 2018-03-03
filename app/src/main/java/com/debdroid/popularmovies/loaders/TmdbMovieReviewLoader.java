package com.debdroid.popularmovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.debdroid.popularmovies.MovieDetailActivity;
import com.debdroid.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by debashispaul on 28/02/2018.
 */

public class TmdbMovieReviewLoader extends AsyncTaskLoader<String> {
    private static final String TAG = "TmdbMovieReviewLoader";

    private final Bundle mBundle;
    private String mReviewJson;

    public TmdbMovieReviewLoader(Context context, Bundle bundle) {
        super(context);
        this.mBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        // If no arguments were passed, nothing to do
        if (mBundle == null) {
            return;
        }

        // If mReviewJson is not empty, deliver that result. Otherwise, force a load
        if (mReviewJson != null) {
            deliverResult(mReviewJson);
        } else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        // Extract the movie id from the args using the key
        int movieId = mBundle.getInt(MovieDetailActivity.MOVIE_ID_EXTRA_KEY);
        // Build the Review url using the movie id
        URL tmdbMovieReviewUrl = NetworkUtils.buildTmdbMovieReviewUrl(movieId);

        try {
            URL reviewUrl = new URL(tmdbMovieReviewUrl.toString());
            return NetworkUtils.getResponseFromTmdbHttpUrl(reviewUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(String data) {
        mReviewJson = data;
        super.deliverResult(data);
    }
}
