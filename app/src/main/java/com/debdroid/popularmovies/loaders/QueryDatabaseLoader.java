package com.debdroid.popularmovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.debdroid.popularmovies.MovieDetailActivity;
import com.debdroid.popularmovies.utils.DatabaseUtils;

/**
 * Created by debashispaul on 28/02/2018.
 */

public class QueryDatabaseLoader extends AsyncTaskLoader<String> {
    private static final String TAG = "QueryDatabaseLoader";

    private final Bundle mQueryBundle;
    private String mQueryReturnStatus;
    public static final String QUERY_FOR_FAVOURITE_MOVIE_SUCCESS = "success";
    public static final String QUERY_FOR_FAVOURITE_MOVIE_FAILURE = "failure";

    public QueryDatabaseLoader(Context context, Bundle bundle) {
        super(context);
        this.mQueryBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        // If no argument is passed then nothing to do, return
        if(mQueryBundle == null) {
            return;
        }

        // If mQueryReturnStatus is not null then deliver the result, otherwise force a load
        if(mQueryReturnStatus != null) {
            deliverResult(mQueryReturnStatus);
        } else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        int movieId = mQueryBundle.getInt(MovieDetailActivity.MOVIE_ID_EXTRA_KEY);
        boolean isPresent = DatabaseUtils.isUserFavouriteMovie(getContext(), movieId);
        if(isPresent) {
            return QUERY_FOR_FAVOURITE_MOVIE_SUCCESS;
        } else {
            return QUERY_FOR_FAVOURITE_MOVIE_FAILURE;
        }
    }

    @Override
    public void deliverResult(String data) {
        mQueryReturnStatus = data;
        super.deliverResult(data);
    }
}
