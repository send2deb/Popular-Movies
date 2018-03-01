package com.debdroid.popularmovies;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.debdroid.popularmovies.loaders.InsertDatabaseLoader;
import com.debdroid.popularmovies.loaders.QueryDatabaseLoader;
import com.debdroid.popularmovies.loaders.TmdbMovieVideoLoader;
import com.debdroid.popularmovies.model.Video;
import com.debdroid.popularmovies.utils.DateUtils;
import com.debdroid.popularmovies.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>, View.OnClickListener,
        VideoAdapter.VideoAdapterOnClickHandler {
    private static final String TAG = "MovieDetailActivity";

    public static final String MOVIE_ID_EXTRA_KEY = "movie_id";
    public static final String MOVIE_TITLE_EXTRA_KEY = "movie_title";
    public static final String MOVIE_RELEASE_DATE_EXTRA_KEY = "movie_release_date";
    public static final String MOVIE_POSTER_PATH_EXTRA_KEY = "movie_poster_path";
    public static final String MOVIE_VOTE_AVERAGE_EXTRA_KEY = "movie_vote_average";
    public static final String MOVIE_PLOT_SYNOPSIS_EXTRA_KEY = "movie_plot_synopsis";
    public static final String MOVIE_BACKDROP_PATH_EXTRA_KEY = "movie_backdrop_path";

    /* Unique identifier for Query Database Loader */
    private static final int QUERY_DATABASE_LOADER = 100;
    /* Unique identifier for Insert into Database Loader */
    private static final int INSERT_DATABASE_LOADER = 200;
    /* Unique identifier for Movie Video Loader */
    private static final int MOVIE_VIDEO_LOADER = 300;

    private boolean isUserFavourite = false;

    private Bundle mBundle;
    private int mMovieId;
    private ImageView mBackdropImage;
    private TextView mMovieTitle;
    private ImageView mMoviePoster;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private TextView mPlotSynopsis;
    private ImageButton mUserFavourite;
    private RecyclerView mVideoRecyclerView;
    private VideoAdapter mVideoAdapter;

    private static final String TMDB_POSTER_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185";
    private static final String TMDB_BACKDROP_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w500";
//    public static final String MOVIE_VIDEO_BUNDLE_EXTRA = "movie_video_extra_key";
//    public static final String MOVIE_REVIEW_BUNDLE_EXTRA = "movie_review_extra_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mBackdropImage = findViewById(R.id.iv_backdrop_image);
        mMovieTitle = findViewById(R.id.tv_movie_title);
        mMoviePoster = findViewById(R.id.iv_poster_image);
        mVoteAverage = findViewById(R.id.tv_vote_average_data);
        mReleaseDate = findViewById(R.id.tv_release_date_data);
        mPlotSynopsis = findViewById(R.id.tv_plot_synopsis_data);
        mUserFavourite = findViewById(R.id.ib_user_favourite);
        mUserFavourite.setOnClickListener(this);
        mVideoRecyclerView = findViewById(R.id.rv_movie_video);


        // Get the mBundle
        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            // Populate the details
            populateMovieDetail();

            // Get the movieId from the bundle
            mMovieId = mBundle.getInt(MOVIE_ID_EXTRA_KEY);
            loadMovieTrailer();

            // Set title
            setTitle(mBundle.getString(MOVIE_TITLE_EXTRA_KEY));

            // Kick-off a loader to query the database to check if the movie is a user favourite
            // movie, if yes then the favourite image button needs to be in accent color
            Bundle bundle = new Bundle();
            bundle.putInt(MOVIE_ID_EXTRA_KEY, mMovieId);
            // Check if the Query Loader already exists using the id, if exists then restarts
            // otherwise create one
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> queryLoader = loaderManager.getLoader(QUERY_DATABASE_LOADER);
            if (queryLoader == null) {
                loaderManager.initLoader(QUERY_DATABASE_LOADER, bundle, this);
            } else {
                loaderManager.restartLoader(QUERY_DATABASE_LOADER, bundle, this);
            }

            // Initialize insert database loaders
            loaderManager.initLoader(INSERT_DATABASE_LOADER, null, this);

            // Kick-off movie video loader
            Loader<String> videoLoader = loaderManager.getLoader(MOVIE_VIDEO_LOADER);
            if (videoLoader == null) {
                loaderManager.initLoader(MOVIE_VIDEO_LOADER, bundle, this);
            } else {
                loaderManager.restartLoader(MOVIE_VIDEO_LOADER, bundle, this);
            }

        }
    }


    private void populateMovieDetail() {
        // Populate the UI fields
        String backdropImagePath = TMDB_BACKDROP_IMAGE_BASE_PATH
                + mBundle.getString(MOVIE_BACKDROP_PATH_EXTRA_KEY);
        Picasso.with(this).load(backdropImagePath).into(mBackdropImage);
        mMovieTitle.setText(mBundle.getString(MOVIE_TITLE_EXTRA_KEY));
        String posterImagePath = TMDB_POSTER_IMAGE_BASE_PATH
                + mBundle.getString(MOVIE_POSTER_PATH_EXTRA_KEY);
        Picasso.with(this).load(posterImagePath).into(mMoviePoster);
        String friendlyDate = DateUtils.formatFriendlyDate
                (mBundle.getString(MOVIE_RELEASE_DATE_EXTRA_KEY));
        mReleaseDate.setText(friendlyDate);
        mVoteAverage.setText(Double.toString(mBundle.getDouble(MOVIE_VOTE_AVERAGE_EXTRA_KEY)));
        mPlotSynopsis.setText(mBundle.getString(MOVIE_PLOT_SYNOPSIS_EXTRA_KEY));
    }

    private void loadMovieTrailer() {
        mVideoAdapter = new VideoAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        mVideoRecyclerView.setLayoutManager(layoutManager);
        //Set this to false for smooth scrolling of Recyclerview
        mVideoRecyclerView.setNestedScrollingEnabled(false);
        //Set this to false so that activity starts the page from the beginning
        mVideoRecyclerView.setFocusable(false);
        // Set this to true for better performance (adapter content is fixed)
        mVideoRecyclerView.setHasFixedSize(true);
        mVideoRecyclerView.setAdapter(mVideoAdapter);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: is called");
        if (isUserFavourite) {
            Toast.makeText(this, "Already favourite", Toast.LENGTH_SHORT).show();
        } else {
            // Kick-off the loader to insert the movie details to database via background thread
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> insertLoader = loaderManager.getLoader(INSERT_DATABASE_LOADER);
            // Check if the Insert Loader already exists using the id, if exists then restarts
            // otherwise create one
            if (insertLoader == null) {
                loaderManager.initLoader(INSERT_DATABASE_LOADER, mBundle, this);
            } else {
                loaderManager.restartLoader(INSERT_DATABASE_LOADER, mBundle, this);
            }
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: loader id -> " + id);
        switch (id) {
            case QUERY_DATABASE_LOADER:
                return new QueryDatabaseLoader(this, args);
            case INSERT_DATABASE_LOADER:
                return new InsertDatabaseLoader(this, args);
            case MOVIE_VIDEO_LOADER:
                return new TmdbMovieVideoLoader(this, args);
            default:
                Log.e(TAG, "onCreateLoader: invalid loader id -> " + id);
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        int id = loader.getId();

        switch (id) {
            case QUERY_DATABASE_LOADER:
                if (data == QueryDatabaseLoader.QUERY_FOR_FAVOURITE_MOVIE_SUCCESS) {
                    isUserFavourite = true;
                    mUserFavourite.setColorFilter(getResources().getColor(R.color.accent),
                            PorterDuff.Mode.SRC_ATOP);
                }
                break;
            case INSERT_DATABASE_LOADER:
                isUserFavourite = true;
                if (data == InsertDatabaseLoader.INSERT_INTO_FAVOURITE_MOVIE_SUCCESS) {
                    mUserFavourite.setColorFilter(getResources().getColor(R.color.accent),
                            PorterDuff.Mode.SRC_ATOP);
                }
                break;
            case MOVIE_VIDEO_LOADER:
                List<Video> videoList = JsonUtils.parseTmdbMovieVideoJson(data);
                mVideoAdapter.swapData(videoList);
                break;
            default:
                Log.e(TAG, "onCreateLoader: invalid loader id -> " + id);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        /* Do nothing */
    }

    @Override
    public void onTrailerItemClick(int position) {
        //TODO - launch youtube
    }
}
