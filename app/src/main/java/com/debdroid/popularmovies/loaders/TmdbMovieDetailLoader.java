package com.debdroid.popularmovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.debdroid.popularmovies.MovieListActivity;
import com.debdroid.popularmovies.model.Movie;
import com.debdroid.popularmovies.utils.DatabaseUtils;
import com.debdroid.popularmovies.utils.JsonUtils;
import com.debdroid.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by debashispaul on 26/02/2018.
 */


public class TmdbMovieDetailLoader extends AsyncTaskLoader<List<Movie>> {
    private static final String TAG = "TmdbMovieDetailLoader";

    private final Bundle mBundle;
//    private final Context myContext;

    public TmdbMovieDetailLoader(Context context, Bundle bundle) {
        super(context);
        this.mBundle = bundle;
//        myContext = context;
    }

    // Create a String member variable to store the raw JSON from TMDb endpoint
//    private String mTmdbJson;
    private List<Movie> mMovieList = new ArrayList<>();

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading: ");

        // If no arguments were passed, nothing to do
        if (mBundle == null) {
            return;
        }

        // If mMovieList is not empty, deliver that result. Otherwise, force a load
//        if(mTmdbJson != null) {
        if(!mMovieList.isEmpty()) {
//        if(mMovieList != null) {
            deliverResult(mMovieList);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Movie> loadInBackground() {
        Log.d(TAG, "loadInBackground: ");
        // Extract the url from the args using the key
        String sortCategory = mBundle.getString(MovieListActivity.MOVIE_QUERY_BUNDLE_EXTRA);
        URL tmdbMovieUrl = NetworkUtils.buildTmdbMovieListUrl(sortCategory);

        // If it's user favourite category then get the data from database
        if(sortCategory.equals(MovieListActivity.TMDB_USER_FAVOURITE_CATEGORY)) {
            // The getContext() method returns the Application context, so leak safe!
            return DatabaseUtils.getUserFavouriteMovie(getContext());
        } else { // If other category then get the data from TMDb endpoint
            // Parse the URL from the passed in String and perform the GET request
            try {
                URL tmdbUrl = new URL(tmdbMovieUrl.toString());
                String response = NetworkUtils.getResponseFromTmdbHttpUrl(tmdbUrl);
                return JsonUtils.parseTmdbMovieJson(response);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void deliverResult(List<Movie> data) {
        Log.d(TAG, "deliverResult() called with: data = [" + data + "]");
//        mTmdbJson = data;
        mMovieList = data;
        super.deliverResult(data);
    }
}
