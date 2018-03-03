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

public class TmdbMovieVideoLoader extends AsyncTaskLoader<String> {
    private static final String TAG = "TmdbMovieVideo";

    private final Bundle mBundle;
    private String mVideoJson;

    public TmdbMovieVideoLoader(Context context, Bundle bundle) {
        super(context);
        this.mBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        // If no arguments were passed, nothing to do
        if (mBundle == null) {
            return;
        }

        // If mVideoJson is not empty, deliver that result. Otherwise, force a load
        if(mVideoJson != null) {
            deliverResult(mVideoJson);
        } else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        // Extract the movie id from the args using the key
        int movieId = mBundle.getInt(MovieDetailActivity.MOVIE_ID_EXTRA_KEY);
        // Build the video url using the movie id
        URL tmdbMovieVideoUrl = NetworkUtils.buildTmdbMovieVideoUrl(movieId);

        try {
            URL videoUrl = new URL(tmdbMovieVideoUrl.toString());
            return NetworkUtils.getResponseFromTmdbHttpUrl(videoUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(String data) {
        mVideoJson = data;
        super.deliverResult(data);
    }
}
