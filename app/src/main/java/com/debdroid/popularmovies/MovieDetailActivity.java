package com.debdroid.popularmovies;

import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.Loader;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.debdroid.popularmovies.model.Movie;
import com.debdroid.popularmovies.utils.DatabaseUtils;
import com.debdroid.popularmovies.utils.DateUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>, View.OnClickListener {
    private static final String TAG = "MovieDetailActivity";

    public static final String MOVIE_ID_EXTRA_KEY = "movie_id";
    public static final String MOVIE_TITLE_EXTRA_KEY = "movie_title";
    public static final String MOVIE_RELEASE_DATE_EXTRA_KEY = "movie_release_date";
    public static final String MOVIE_POSTER_PATH_EXTRA_KEY = "movie_poster_path";
    public static final String MOVIE_VOTE_AVERAGE_EXTRA_KEY = "movie_vote_average";
    public static final String MOVIE_PLOT_SYNOPSIS_EXTRA_KEY = "movie_plot_synopsis";
    public static final String MOVIE_BACKDROP_PATH_EXTRA_KEY = "movie_backdrop_path";

    private static final String QUERY_FOR_FAVOURITE_MOVIE_SUCCESS = "success";
    private static final String QUERY_FOR_FAVOURITE_MOVIE_FAILURE = "failure";
    private static final String INSERT_INTO_FAVOURITE_MOVIE_SUCCESS = "success";
    private static final String INSERT_INTO_FAVOURITE_MOVIE_FAILURE = "failure";


    /* Unique identifier for Query Database Loader */
    private static final int QUERY_DATABASE_LOADER = 100;
    /* Unique identifier for Insert into Database Loader */
    private static final int INSERT_DATABASE_LOADER = 200;

    private boolean isUserFavourite = false;

    private Bundle mBundle;
    private int mMovieId;
    private ImageButton mUserFavourite;

    private final static String TMDB_POSTER_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185";
    private final static String TMDB_BACKDROP_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w500";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ImageView mBackdropImage = findViewById(R.id.iv_backdrop_image);
        TextView mMovieTitle = findViewById(R.id.tv_movie_title);
        ImageView mMoviePoster = findViewById(R.id.iv_poster_image);
        TextView mVoteAverage = findViewById(R.id.tv_vote_average_data);
        TextView mReleaseDate = findViewById(R.id.tv_release_date_data);
        TextView mPlotSynopsis = findViewById(R.id.tv_plot_synopsis_data);
        mUserFavourite = findViewById(R.id.ib_user_favourite);
        mUserFavourite.setOnClickListener(this);


        // Get the mBundle
        mBundle = getIntent().getExtras();
        if(mBundle != null) {
            // Populate the UI fields
            String backdropImagePath = TMDB_BACKDROP_IMAGE_BASE_PATH
                    + mBundle.getString(MOVIE_BACKDROP_PATH_EXTRA_KEY);
            Picasso.with(this).load(backdropImagePath).into(mBackdropImage);
            mMovieTitle.setText(mBundle.getString(MOVIE_TITLE_EXTRA_KEY));
            String posterImagePath = TMDB_POSTER_IMAGE_BASE_PATH
                    + mBundle.getString(MOVIE_POSTER_PATH_EXTRA_KEY);
            Picasso.with(this).load(posterImagePath).into(mMoviePoster);
            String friendlyDAte = DateUtils.formatFriendlyDate
                    (mBundle.getString(MOVIE_RELEASE_DATE_EXTRA_KEY));
            mReleaseDate.setText(friendlyDAte);
            mVoteAverage.setText(Double.toString(mBundle.getDouble(MOVIE_VOTE_AVERAGE_EXTRA_KEY)));
            mPlotSynopsis.setText(mBundle.getString(MOVIE_PLOT_SYNOPSIS_EXTRA_KEY));

            // Kick-off a loader to query the database to check if the movie is a user favourite
            // movie, if yes then the favourite image button needs to be in accent color
            Bundle bundle = new Bundle();
            bundle.putInt(MOVIE_ID_EXTRA_KEY, mBundle.getInt(MOVIE_ID_EXTRA_KEY));
            // Check if the Query Loader already exists using the id, if exists then restarts
            // otherwise create one
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> queryLoader = loaderManager.getLoader(QUERY_DATABASE_LOADER);
            if (queryLoader == null) {
                loaderManager.initLoader(QUERY_DATABASE_LOADER, bundle, this);
            } else {
                loaderManager.restartLoader(QUERY_DATABASE_LOADER, bundle, this);
            }

            // Initialize other loaders
            loaderManager.initLoader(INSERT_DATABASE_LOADER, null, this);
        }
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: is called");
        if(isUserFavourite) {
            Toast.makeText(this, "Already favourite", Toast.LENGTH_SHORT).show();
        } else {
            // Kick-off the loader to insert the movie details to database via background thread
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> insertLoader = loaderManager.getLoader(INSERT_DATABASE_LOADER);
            // Check if the Insert Loader already exists using the id, if exists then restarts
            // otherwise create one
            if(insertLoader == null) {
                loaderManager.initLoader(INSERT_DATABASE_LOADER, mBundle, this);
            } else {
                loaderManager.restartLoader(INSERT_DATABASE_LOADER, mBundle, this);
            }
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: loader id -> " + id);
        switch(id) {
            case QUERY_DATABASE_LOADER:
                return new QueryDatabaseInBackground(this, args);
            case INSERT_DATABASE_LOADER:
                return new InsertIntoDatabaseInBackground(this, args);
            default:
                Log.e(TAG, "onCreateLoader: invalid loader id -> " + id);
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        int id = loader.getId();

        Log.d(TAG, "onLoadFinished: data -> " + data);
        Log.d(TAG, "onLoadFinished: id -> " + id);

        switch (id) {
            case QUERY_DATABASE_LOADER:
                if(data == QUERY_FOR_FAVOURITE_MOVIE_SUCCESS) {
                    isUserFavourite = true;
                    mUserFavourite.setColorFilter(getResources().getColor(R.color.accent),
                            PorterDuff.Mode.SRC_ATOP);
                }
                break;
            case INSERT_DATABASE_LOADER:
                isUserFavourite = true;
                if(data == INSERT_INTO_FAVOURITE_MOVIE_SUCCESS) {
                    mUserFavourite.setColorFilter(getResources().getColor(R.color.accent),
                            PorterDuff.Mode.SRC_ATOP);
                }
                break;
            default:
                Log.e(TAG, "onCreateLoader: invalid loader id -> " + id);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        /* Do nothing */
    }

    private static class QueryDatabaseInBackground extends AsyncTaskLoader<String> {

        private final Bundle mQueryBundle;
        private String mQueryReturnStatus;

        private QueryDatabaseInBackground(Context context, Bundle bundle) {
            super(context);
            this.mQueryBundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            // If no argument is passed then nothing to do, return
            if(mQueryBundle == null) {
                Log.d(TAG, "QueryDatabaseInBackground:onStartLoading: bundle is null");
                return;
            } else {
                Log.d(TAG, "QueryDatabaseInBackground:onStartLoading: bundle is NOT null");
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
            int movieId = mQueryBundle.getInt(MOVIE_ID_EXTRA_KEY);
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

    private static class InsertIntoDatabaseInBackground extends AsyncTaskLoader<String> {

        private final Bundle mInsertBundle;
        private String mInsertReturnStatus;

        private InsertIntoDatabaseInBackground(Context context, Bundle bundle) {
            super(context);
            this.mInsertBundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            // If no argument is passed then nothing to do, return
            if(mInsertBundle == null) {
                Log.d(TAG, "InsertIntoDatabaseInBackground:onStartLoading: bundle is null");
                return;
            } else {
                Log.d(TAG, "InsertIntoDatabaseInBackground:onStartLoading: bundle is NOT null");
            }

            // If mInsertReturnStatus is not null then deliver the result, otherwise force a load
            if(mInsertReturnStatus != null) {
                deliverResult(mInsertReturnStatus);
            } else {
                forceLoad();
            }
        }

        @Override
        public String loadInBackground() {
            Log.d(TAG, "InsertIntoDatabaseInBackground:loadInBackground: is called");
            Movie movie = new Movie();
            movie.setmMovieId(mInsertBundle.getInt(MOVIE_ID_EXTRA_KEY));
            movie.setmOriginalTitle(mInsertBundle.getString(MOVIE_TITLE_EXTRA_KEY));
            movie.setmPosterImage(mInsertBundle.getString(MOVIE_POSTER_PATH_EXTRA_KEY));
            movie.setmReleaseDate(mInsertBundle.getString(MOVIE_RELEASE_DATE_EXTRA_KEY));
            movie.setmUserRating(mInsertBundle.getDouble(MOVIE_VOTE_AVERAGE_EXTRA_KEY));
            movie.setmBackdropImage(mInsertBundle.getString(MOVIE_BACKDROP_PATH_EXTRA_KEY));
            movie.setmPlotSynopsis(mInsertBundle.getString(MOVIE_PLOT_SYNOPSIS_EXTRA_KEY));

            Uri uri = DatabaseUtils.insertUserFavouriteMovie(getContext(), movie);
            Log.d(TAG, "loadInBackground: uri" + uri);
            if(uri != null) {
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
}
