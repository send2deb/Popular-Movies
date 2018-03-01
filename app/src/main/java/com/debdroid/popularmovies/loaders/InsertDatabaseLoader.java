package com.debdroid.popularmovies.loaders;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.debdroid.popularmovies.MovieDetailActivity;
import com.debdroid.popularmovies.model.Movie;
import com.debdroid.popularmovies.utils.DatabaseUtils;

/**
 * Created by debashispaul on 28/02/2018.
 */

public class InsertDatabaseLoader extends AsyncTaskLoader<String> {
    private static final String TAG = "InsertDatabaseLoader";

    private final Bundle mInsertBundle;
    private String mInsertReturnStatus;
    public static final String INSERT_INTO_FAVOURITE_MOVIE_SUCCESS = "success";
    public static final String INSERT_INTO_FAVOURITE_MOVIE_FAILURE = "failure";

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
        Log.d(TAG, "InsertIntoDatabaseInBackground:loadInBackground: is called");
        Movie movie = new Movie();
        movie.setmMovieId(mInsertBundle.getInt(MovieDetailActivity.MOVIE_ID_EXTRA_KEY));
        movie.setmOriginalTitle(mInsertBundle.getString(MovieDetailActivity.MOVIE_TITLE_EXTRA_KEY));
        movie.setmPosterImage(mInsertBundle
                .getString(MovieDetailActivity.MOVIE_POSTER_PATH_EXTRA_KEY));
        movie.setmReleaseDate(mInsertBundle
                .getString(MovieDetailActivity.MOVIE_RELEASE_DATE_EXTRA_KEY));
        movie.setmUserRating(mInsertBundle
                .getDouble(MovieDetailActivity.MOVIE_VOTE_AVERAGE_EXTRA_KEY));
        movie.setmBackdropImage(mInsertBundle
                .getString(MovieDetailActivity.MOVIE_BACKDROP_PATH_EXTRA_KEY));
        movie.setmPlotSynopsis(mInsertBundle
                .getString(MovieDetailActivity.MOVIE_PLOT_SYNOPSIS_EXTRA_KEY));

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
