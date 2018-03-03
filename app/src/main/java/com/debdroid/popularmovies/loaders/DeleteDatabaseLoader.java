package com.debdroid.popularmovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.debdroid.popularmovies.MovieDetailActivity;
import com.debdroid.popularmovies.utils.DatabaseUtils;

/**
 * Created by debashispaul on 03/03/2018.
 */

public class DeleteDatabaseLoader extends AsyncTaskLoader<String> {
    private static final String TAG = "DeleteDatabaseLoader";

    private final Bundle mDeleteBundle;
    private String mDeleteReturnStatus;
    public static final String DELETE_FROM_FAVOURITE_MOVIE_SUCCESS = "delete_success";
    public static final String DELETE_FROM_FAVOURITE_MOVIE_FAILURE = "delete_failure";

    public DeleteDatabaseLoader(Context context, Bundle bundle) {
        super(context);
        this.mDeleteBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        // If no argument is passed then nothing to do, return
        if (mDeleteBundle == null) {
            return;
        } else

            // If mDeleteReturnStatus is not null then deliver the result, otherwise force a load
            if (mDeleteReturnStatus != null) {
                deliverResult(mDeleteReturnStatus);
            } else {
                forceLoad();
            }
    }

    @Override
    public String loadInBackground() {
        int movieId = mDeleteBundle.getInt(MovieDetailActivity.MOVIE_ID_EXTRA_KEY);

        int retValue = DatabaseUtils.deleteUserFavouriteMovie(getContext(), movieId);
        if (retValue == 1) {
            return DELETE_FROM_FAVOURITE_MOVIE_SUCCESS;
        } else {
            return DELETE_FROM_FAVOURITE_MOVIE_FAILURE;
        }
    }

    @Override
    public void deliverResult(String data) {
        mDeleteReturnStatus = data;
        super.deliverResult(data);
    }
}
