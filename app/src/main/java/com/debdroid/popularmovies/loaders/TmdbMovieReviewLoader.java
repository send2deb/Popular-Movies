package com.debdroid.popularmovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.debdroid.popularmovies.MovieDetailActivity;
import com.debdroid.popularmovies.model.Review;
import com.debdroid.popularmovies.utils.JsonUtils;
import com.debdroid.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by debashispaul on 28/02/2018.
 */

public class TmdbMovieReviewLoader extends AsyncTaskLoader<List<Review>> {
    private static final String TAG = "TmdbReviewLoader";

    private final Bundle mBundle;
    private List<Review> mReviewList = new ArrayList<>();

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

        // If mReviewList is not empty, deliver that result. Otherwise, force a load
        if(!mReviewList.isEmpty()) {
            deliverResult(mReviewList);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Review> loadInBackground() {
        // Extract the movie id from the args using the key
        int movieId = mBundle.getInt(MovieDetailActivity.MOVIE_ID_EXTRA_KEY);
        // Build the review url using the movie id
        URL tmdbMovieReviewUrl = NetworkUtils.buildTmdbMovieReviewUrl(movieId);

        try {
            URL reviewUrl = new URL(tmdbMovieReviewUrl.toString());
            String response = NetworkUtils.getResponseFromTmdbHttpUrl(reviewUrl);
            return JsonUtils.parseTmdbMovieReviewJson(response);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(List<Review> data) {
        mReviewList = data;
        super.deliverResult(data);
    }
}
