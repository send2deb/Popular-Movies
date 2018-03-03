package com.debdroid.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.debdroid.popularmovies.adapters.ReviewAdapter;
import com.debdroid.popularmovies.adapters.VideoAdapter;
import com.debdroid.popularmovies.loaders.DeleteDatabaseLoader;
import com.debdroid.popularmovies.loaders.InsertDatabaseLoader;
import com.debdroid.popularmovies.loaders.QueryDatabaseLoader;
import com.debdroid.popularmovies.loaders.TmdbMovieReviewLoader;
import com.debdroid.popularmovies.loaders.TmdbMovieVideoLoader;
import com.debdroid.popularmovies.model.Review;
import com.debdroid.popularmovies.model.Video;
import com.debdroid.popularmovies.utils.DateUtils;
import com.debdroid.popularmovies.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>, View.OnClickListener,
        VideoAdapter.VideoAdapterOnClickHandler, ReviewAdapter.ReviewAdapterOnClickHandler {
    private static final String TAG = "MovieDetailActivity";

    /* Define the static variables used as key for bundle parameters */
    public static final String MOVIE_ID_EXTRA_KEY = "movie_id";
    public static final String MOVIE_TITLE_EXTRA_KEY = "movie_title";
    public static final String MOVIE_RELEASE_DATE_EXTRA_KEY = "movie_release_date";
    public static final String MOVIE_POSTER_PATH_EXTRA_KEY = "movie_poster_path";
    public static final String MOVIE_VOTE_AVERAGE_EXTRA_KEY = "movie_vote_average";
    public static final String MOVIE_PLOT_SYNOPSIS_EXTRA_KEY = "movie_plot_synopsis";
    public static final String MOVIE_BACKDROP_PATH_EXTRA_KEY = "movie_backdrop_path";
    public static final String MOVIE_REVIEW_TITLE_BUNDLE_EXTRA = "movie_review_title_key";
    public static final String MOVIE_REVIEW_AUTHOR_BUNDLE_EXTRA = "movie_review_author_key";
    public static final String MOVIE_REVIEW_CONTENT_BUNDLE_EXTRA = "movie_review_bundle_key";

    /* Static variables for TMDb endpoints */
    private static final String TMDB_POSTER_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185";
    private static final String TMDB_BACKDROP_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w500";

    /* Unique identifier for Query Database Loader */
    private static final int QUERY_DATABASE_LOADER = 100;
    /* Unique identifier for Insert into Database Loader */
    private static final int INSERT_DATABASE_LOADER = 200;
    /* Unique identifier for Insert into Database Loader */
    private static final int DELETE_DATABASE_LOADER = 300;
    /* Unique identifier for Movie Video Loader */
    private static final int MOVIE_VIDEO_LOADER = 400;
    /* Unique identifier for Movie Review Loader */
    private static final int MOVIE_REVIEW_LOADER = 500;

    /* Define variables for UI elements */
    private ImageView mBackdropImageView;
    private TextView mMovieTitleTextView;
    private ImageView mMoviePosterTextView;
    private TextView mVoteAverageTextView;
    private TextView mReleaseDateTextView;
    private TextView mPlotSynopsisTextView;
    private ImageButton mUserFavouriteImageButton;
    private RecyclerView mVideoRecyclerView;
    private RecyclerView mReviewRecyclerView;

    /* Define the adapters */
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;

    /* Other memebr variables */
    private Bundle mBundle;
    private boolean isUserFavourite = false;
    private int mMovieId;
    private String mMovieOriginalTitle;
    private String mMovieReleaseDate;
    private String mMovieBackdropImage;
    private String mMoviePoster;
    private String mMoviePlotSynopsis;
    private double mMovieVoteAverage;
    private List<Review> mReviewList = new ArrayList<>();
    private ShareActionProvider mShareActionProvider;
    private String mYouTubeFirstTrailerKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mBackdropImageView = findViewById(R.id.iv_backdrop_image);
        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMoviePosterTextView = findViewById(R.id.iv_poster_image);
        mVoteAverageTextView = findViewById(R.id.tv_vote_average_data);
        mReleaseDateTextView = findViewById(R.id.tv_release_date_data);
        mPlotSynopsisTextView = findViewById(R.id.tv_plot_synopsis_data);
        mUserFavouriteImageButton = findViewById(R.id.ib_user_favourite);
        mVideoRecyclerView = findViewById(R.id.rv_movie_video);
        mReviewRecyclerView = findViewById(R.id.rv_movie_review);

        // Set the click listener to user favourite button
        mUserFavouriteImageButton.setOnClickListener(this);

        // If it's first time then get the Bundle from intent otherwise from savedInstanceState
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: First Time - savedInstanceState is NULL");
            mBundle = getIntent().getExtras();
        } else {
            Log.d(TAG, "onCreate: Restore Case - savedInstanceState is NOT NULL");
            mBundle = savedInstanceState;
        }
        if (mBundle != null) {
            mMovieId = mBundle.getInt(MOVIE_ID_EXTRA_KEY);
            mMovieOriginalTitle = mBundle.getString(MOVIE_TITLE_EXTRA_KEY);
            mMovieReleaseDate = mBundle.getString(MOVIE_RELEASE_DATE_EXTRA_KEY);
            mMovieBackdropImage = mBundle.getString(MOVIE_BACKDROP_PATH_EXTRA_KEY);
            mMoviePoster = mBundle.getString(MOVIE_POSTER_PATH_EXTRA_KEY);
            mMoviePlotSynopsis = mBundle.getString(MOVIE_PLOT_SYNOPSIS_EXTRA_KEY);
            mMovieVoteAverage = mBundle.getDouble(MOVIE_VOTE_AVERAGE_EXTRA_KEY);

            // Populate the UI details
            populateMovieDetail();

            // Set the RecyclerView of movie trailer
            setMovieTrailerRecyclerView();

            // Set the RecyclerView of movie review
            setMovieReviewRecyclerView();

            // Set Activity title to Movie name
            setTitle(mMovieOriginalTitle);

            // Kick-off a loader to query the database to check if the movie is a user favourite
            // movie and set the flag and button color accordingly
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

            // Kick-off movie video loader to load data in the background
            Loader<String> videoLoader = loaderManager.getLoader(MOVIE_VIDEO_LOADER);
            if (videoLoader == null) {
                loaderManager.initLoader(MOVIE_VIDEO_LOADER, bundle, this);
            } else {
                loaderManager.restartLoader(MOVIE_VIDEO_LOADER, bundle, this);
            }

            // Kick-off movie review loader to load data in the background
            Loader<String> reviewLoader = loaderManager.getLoader(MOVIE_REVIEW_LOADER);
            if (reviewLoader == null) {
                loaderManager.initLoader(MOVIE_REVIEW_LOADER, bundle, this);
            } else {
                loaderManager.restartLoader(MOVIE_REVIEW_LOADER, bundle, this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Store all the data passed from MovieListActivity so that it can be retrieved in case of
        // orientation change
        outState.putInt(MOVIE_ID_EXTRA_KEY, mMovieId);
        outState.putString(MOVIE_TITLE_EXTRA_KEY, mMovieOriginalTitle);
        outState.putString(MOVIE_POSTER_PATH_EXTRA_KEY, mMoviePoster);
        outState.putString(MOVIE_BACKDROP_PATH_EXTRA_KEY, mMovieBackdropImage);
        outState.putString(MOVIE_RELEASE_DATE_EXTRA_KEY, mMovieReleaseDate);
        outState.putString(MOVIE_PLOT_SYNOPSIS_EXTRA_KEY, mMoviePlotSynopsis);
        outState.putDouble(MOVIE_VOTE_AVERAGE_EXTRA_KEY, mMovieVoteAverage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.movie_detail_activity_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem menuItem = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Set the share action intent
        setShareActionIntent();

        // Return true
        return true;
    }

    /**
     * This method populates the UI fields for movie detail
     */
    private void populateMovieDetail() {
        // Populate the UI fields
        String backdropImagePath = TMDB_BACKDROP_IMAGE_BASE_PATH + mMovieBackdropImage;
        Picasso.with(this).load(backdropImagePath).into(mBackdropImageView);
        mMovieTitleTextView.setText(mMovieOriginalTitle);
        String posterImagePath = TMDB_POSTER_IMAGE_BASE_PATH + mMoviePoster;
        Picasso.with(this).load(posterImagePath).into(mMoviePosterTextView);
        String friendlyDate = DateUtils.formatFriendlyDate(mMovieReleaseDate);
        mReleaseDateTextView.setText(friendlyDate);
        mVoteAverageTextView.setText(Double.toString(mMovieVoteAverage));
        mPlotSynopsisTextView.setText(mMoviePlotSynopsis);
    }

    /**
     * This method hook up the trailer RecyclerView with the adapter
     */
    private void setMovieTrailerRecyclerView() {
        mVideoAdapter = new VideoAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        mVideoRecyclerView.setLayoutManager(layoutManager);
        //Set this to false for smooth scrolling of RecyclerView
        mVideoRecyclerView.setNestedScrollingEnabled(false);
        //Set this to false so that activity starts the page from the beginning
        mVideoRecyclerView.setFocusable(false);
        // Set this to true for better performance (adapter content is fixed)
        mVideoRecyclerView.setHasFixedSize(true);
        mVideoRecyclerView.setAdapter(mVideoAdapter);
        // Set divider ItemDecoration
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        mVideoRecyclerView.addItemDecoration(itemDecoration);
    }

    /**
     * This method hook up the review RecyclerView with the adapter
     */
    private void setMovieReviewRecyclerView() {
        mReviewAdapter = new ReviewAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        mReviewRecyclerView.setLayoutManager(layoutManager);
        //Set this to false for smooth scrolling of RecyclerView
        mReviewRecyclerView.setNestedScrollingEnabled(false);
        //Set this to false so that activity starts the page from the beginning
        mReviewRecyclerView.setFocusable(false);
        // Set this to true for better performance (adapter content is fixed)
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        // Set divider ItemDecoration
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        mReviewRecyclerView.addItemDecoration(itemDecoration);
    }

    /**
     * This method shares the first trailer’s YouTube URL
     */
    private void setShareActionIntent() {
        String data = "http://www.youtube.com/watch?v=" + mYouTubeFirstTrailerKey;
        Intent youTubeShareIntent = new Intent(Intent.ACTION_SEND);
        youTubeShareIntent.setType("text/plain");
        youTubeShareIntent.putExtra(Intent.EXTRA_TEXT, data);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(youTubeShareIntent);
        }
    }

    /**
     * This method is called whenever the user favourite is clicked
     *
     * @param v View (i.e. user favourite button) that has been clicked
     */
    @Override
    public void onClick(View v) {
        if (isUserFavourite) { // The movie is in user favourite list, so need to delete it
            // Kick-off the loader to delete the movie from database via background thread
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> insertLoader = loaderManager.getLoader(DELETE_DATABASE_LOADER);
            // Check if the Insert Loader already exists using the id, if exists then restarts
            // otherwise create one
            if (insertLoader == null) {
                loaderManager.initLoader(DELETE_DATABASE_LOADER, mBundle, this);
            } else {
                loaderManager.restartLoader(DELETE_DATABASE_LOADER, mBundle, this);
            }
        } else { // The movie is not in user favourite list, so need to insert it
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
        switch (id) {
            case QUERY_DATABASE_LOADER:
                return new QueryDatabaseLoader(this, args);
            case INSERT_DATABASE_LOADER:
                return new InsertDatabaseLoader(this, args);
            case DELETE_DATABASE_LOADER:
                return new DeleteDatabaseLoader(this, args);
            case MOVIE_VIDEO_LOADER:
                return new TmdbMovieVideoLoader(this, args);
            case MOVIE_REVIEW_LOADER:
                return new TmdbMovieReviewLoader(this, args);
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
                if (data.equals(QueryDatabaseLoader.QUERY_FOR_FAVOURITE_MOVIE_SUCCESS)) {
                    isUserFavourite = true;
                    setUserFavouriteButtonColor(isUserFavourite);
                } else {
                    isUserFavourite = false;
                    setUserFavouriteButtonColor(isUserFavourite);
                }
                break;
            case INSERT_DATABASE_LOADER:
                if (data.equals(InsertDatabaseLoader.INSERT_INTO_FAVOURITE_MOVIE_SUCCESS)) {
                    isUserFavourite = true;
                    setUserFavouriteButtonColor(isUserFavourite);
                    Toast.makeText(this, "Movie " + mMovieOriginalTitle +
                            " added to favourite list", Toast.LENGTH_SHORT).show();
                }
                break;
            case DELETE_DATABASE_LOADER:
                if (data.equals(DeleteDatabaseLoader.DELETE_FROM_FAVOURITE_MOVIE_SUCCESS)) {
                    isUserFavourite = false;
                    setUserFavouriteButtonColor(isUserFavourite);
                    Toast.makeText(this, "Movie " + mMovieOriginalTitle +
                            " removed from favourite list", Toast.LENGTH_SHORT).show();
                }
                break;
            case MOVIE_VIDEO_LOADER:
                List<Video> videoList = JsonUtils.parseTmdbMovieVideoJson(data);
                if (videoList.size() > 0) { // If there is no trailer then hide the Trailer label
                    findViewById(R.id.tv_trailer_label).setVisibility(View.VISIBLE);
                    mVideoAdapter.swapData(videoList);
                    mYouTubeFirstTrailerKey = videoList.get(0).getmKey();
                    // Update the share action intent
                    setShareActionIntent();
                } else {
                    findViewById(R.id.tv_trailer_label).setVisibility(View.GONE);
                }
                break;
            case MOVIE_REVIEW_LOADER:
                mReviewList = JsonUtils.parseTmdbMovieReviewJson(data);
                if (mReviewList.size() > 0) {// If there is no review then hide the review label
                    findViewById(R.id.tv_review_label).setVisibility(View.VISIBLE);
                    mReviewAdapter.swapData(mReviewList);
                } else {
                    findViewById(R.id.tv_review_label).setVisibility(View.GONE);
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

    /**
     * This method sets the color of the user favourite button - Accent if On, otherwise Black
     *
     * @param isTrue Flag to determine if the movie is a user favourite movie
     */
    private void setUserFavouriteButtonColor(boolean isTrue) {
        if (isTrue) {
            mUserFavouriteImageButton.setColorFilter(getResources().getColor(R.color.accent),
                    PorterDuff.Mode.SRC_ATOP);
        } else {
            mUserFavouriteImageButton.setColorFilter(getResources()
                    .getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * Implementation of callback interface method of movie trailer adapter
     *
     * @param videoId The associated video id of the RecyclerView item that got clicked
     */
    @Override
    public void onTrailerItemClick(String videoId) {
        // Create YouTube intent for YouTube app
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
        // Create YouTube intent for Web
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoId));
        try { // First try to play in YouTube app
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) { // If app is not present then play in Web
            startActivity(webIntent);
        }
    }

    /**
     * Implementation of callback interface method of movie review adapter
     *
     * @param position The position of the RecyclerView item that got clicked
     */
    @Override
    public void onReviewItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_REVIEW_TITLE_BUNDLE_EXTRA, mMovieOriginalTitle);
        bundle.putString(MOVIE_REVIEW_AUTHOR_BUNDLE_EXTRA, mReviewList.get(position).getmAuthor());
        bundle.putString(MOVIE_REVIEW_CONTENT_BUNDLE_EXTRA, mReviewList.get(position).getmContent());

        // Create and start detail Review page
        Intent reviewIntent = new Intent(this, ReviewActivity.class);
        reviewIntent.putExtras(bundle);
        startActivity(reviewIntent);
    }
}
