package com.debdroid.popularmovies.loaders;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.debdroid.popularmovies.MovieListActivity;
import com.debdroid.popularmovies.model.Movie;
import com.debdroid.popularmovies.utils.DatabaseUtils;

/**
 * Created by debashispaul on 28/02/2018.
 */

public class InsertDatabaseLoader extends AsyncTaskLoader<String> {
    private static final String TAG = "InsertDatabaseLoader";

    private final Bundle mInsertBundle;
    private String mInsertReturnStatus;
    public static final String INSERT_INTO_FAVOURITE_MOVIE_SUCCESS = "insert_success";
    public static final String INSERT_INTO_FAVOURITE_MOVIE_FAILURE = "insert_failure";

    public InsertDatabaseLoader(Context context, Bundle bundle) {
        super(context);
        this.mInsertBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        // If no argument is passed then nothing to do, return
        if (mInsertBundle == null) {
            return;
        } else

            // If mInsertReturnStatus is not null then deliver the result, otherwise force a load
            if (mInsertReturnStatus != null) {
                deliverResult(mInsertReturnStatus);
            } else {
                forceLoad();
            }
    }

    @Override
    public String loadInBackground() {
        Movie movie = mInsertBundle.getParcelable(MovieListActivity.MOVIE_PARCELABLE_EXTRA_KEY);

        Uri uri = DatabaseUtils.insertUserFavouriteMovie(getContext(), movie);
        if (uri != null) {
            return INSERT_INTO_FAVOURITE_MOVIE_SUCCESS;
        } else {
            return INSERT_INTO_FAVOURITE_MOVIE_FAILURE;
        }
    }

    @Override
    public void deliverResult(String data) {
        mInsertReturnStatus = data;
        super.deliverResult(data);
    }
}
