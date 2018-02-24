package com.debdroid.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.debdroid.popularmovies.model.Movie;
import com.debdroid.popularmovies.utils.JsonUtils;
import com.debdroid.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,
        MovieAdapter.MovieGAdapterOnClickHandler {

    private final static String LOG_TAG = MovieList.class.getSimpleName();

    /* A constant to save and restore the URL that is being displayed */
    private static final String TMDB_QUERY_URL_EXTRA = "query";

    /* Unique identifier for Loader */
    private static final int TMDB_MOVIES_LOADER = 84;

    /* TMDb category for popular movie */
    private static final String TMDB_POPULAR_MOVIE_CATEGORY = "popular";

    /* TMDb category for popular movie */
    private static final String TMDB_TOP_RATED_MOVIE_CATEGORY = "top_rated";

    private List<Movie> mMovieList = new ArrayList<>();

    private String mSortCategory;

    private TextView mErrorMessage;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private RecyclerView.LayoutManager mMovieGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        mErrorMessage = findViewById(R.id.tv_error_message_display);
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.rv_movie_list);
        mMovieAdapter = new MovieAdapter(this, this);
        mMovieGridLayoutManager = new GridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        mMovieGridLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mMovieGridLayoutManager);
        //Set this to false for smooth scrolling of Recyclerview
        mRecyclerView.setNestedScrollingEnabled(false);
        //Set this to false so that activity starts the page from the beginning
        mRecyclerView.setFocusable(false);
        // Set this to true for better performance (adapter content is fixed)
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMovieAdapter);

        /*
         * Initialize the loader
         */
        getSupportLoaderManager().initLoader(TMDB_MOVIES_LOADER, null, this);

        // Default sort category is most popular
        mSortCategory = TMDB_POPULAR_MOVIE_CATEGORY;
        makeTmdbMovieQuery();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.movie_list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // It's the top level menu, no action required just get the icon
            case R.id.menu_action_sort:
                // It's the top level menu, no action required just get the icon
                return true;
            case R.id.menu_action_sort_most_popular:
                mSortCategory = TMDB_POPULAR_MOVIE_CATEGORY;
                makeTmdbMovieQuery();
                return true;
            case R.id.menu_action_sort_highest_rated:
                mSortCategory = TMDB_TOP_RATED_MOVIE_CATEGORY;
                makeTmdbMovieQuery();
                return true;
            default:
                Log.e(LOG_TAG, "Menu selection error");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method constructs the URL (using {@link NetworkUtils}) for the TMDb endpoint and
     * starts the AsyncTaskLoader to perform the GET request.
     */
    private void makeTmdbMovieQuery() {
        // Check if network connectivity is available, otherwise return with a Toast message
        if(!NetworkUtils.isOnline(this)) {
            Toast.makeText(this, "No internet connection.",Toast.LENGTH_LONG).show();
            return;
        }

        // Get the URL for TMDb endpoint from NetworkUtils
        URL tmdbMovieUrl = NetworkUtils.buildTmdbMovieListUrl(mSortCategory);

        // Create a bundle with the URL to pass to Loader
        Bundle queryBundle = new Bundle();
        queryBundle.putString(TMDB_QUERY_URL_EXTRA, tmdbMovieUrl.toString());

        // Check if the Loader already exists using the id, if exists then restarts otherwise
        // create one
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> tmdbMovieLoader = loaderManager.getLoader(TMDB_MOVIES_LOADER);
        if (tmdbMovieLoader == null) {
            loaderManager.initLoader(TMDB_MOVIES_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(TMDB_MOVIES_LOADER, queryBundle, this);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {

        // Show the loading indicator only when it's online and trying to load the data
        if(NetworkUtils.isOnline(this)) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        return new LoadDataInBackground(this, args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        /* When we finish loading, hide the loading indicator from the user. */
        mProgressBar.setVisibility(View.INVISIBLE);
        /*
         * If the results are null, we assume an error has occurred. There are much more robust
         * methods for checking errors, but we wanted to keep this particular example simple.
         */
        if (data == null) {
            showErrorMessage();
        } else {
            mMovieList = JsonUtils.parseTmdbMovieJson(data);
            mMovieAdapter.swapData(mMovieList);
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        /* Do nothing */
    }

    private void showErrorMessage() {
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Override the interface method to interact with click even
     * @param position Position of the movie poster clicked
     */
    @Override
    public void onMovieItemClick(int position) {
        Intent startMovieDetailIntent = new Intent(this, MovieDetail.class);
        Bundle bundle = new Bundle();
        bundle.putString(MovieDetail.MOVIE_TITLE_EXTRA_KEY, mMovieList.get(position)
                .getmOriginalTitle());
        bundle.putString(MovieDetail.MOVIE_RELEASE_DATE_EXTRA_KEY, mMovieList.get(position)
                .getmReleaseDate());
        bundle.putString(MovieDetail.MOVIE_POSTER_PATH_EXTRA_KEY, mMovieList.get(position)
                .getmPosterImage());
        bundle.putDouble(MovieDetail.MOVIE_VOTE_AVERAGE_EXTRA_KEY, mMovieList.get(position)
                .getmUserRating());
        bundle.putString(MovieDetail.MOVIE_PLOT_SYNOPSIS_EXTRA_KEY, mMovieList.get(position)
                .getmPlotSynopsis());
        bundle.putString(MovieDetail.MOVIE_BACKDROP_PATH_EXTRA_KEY, mMovieList.get(position)
                .getmBackdropImage());
        startMovieDetailIntent.putExtras(bundle);
        startActivity(startMovieDetailIntent);
    }

    /**
     * This static inner class loads the data from Tmdb endpoint over internet in background thread
     * using AsyncTaskLoader.
     * Android Studio complained when used as anonymous class, so an inner static class is created.
     */
    static class LoadDataInBackground extends AsyncTaskLoader<String> {

        private final Bundle mBundle;

        public LoadDataInBackground(Context context, Bundle bundle) {
            super(context);
            this.mBundle = bundle;
        }

        // Create a String member variable to store the raw JSON from TMDb endpoint
        private String mTmdbJson;

        @Override
        protected void onStartLoading() {

            // If no arguments were passed, nothing to do
            if (mBundle == null) {
                return;
            }

            // If mTmdbJson is not null, deliver that result. Otherwise, force a load
            if(mTmdbJson != null) {
                deliverResult(mTmdbJson);
            } else {
                forceLoad();
            }
        }

        @Override
        public String loadInBackground() {
            // Extract the url from the args using the key
            String tmdbQueryUrlString = mBundle.getString(TMDB_QUERY_URL_EXTRA);

            // Parse the URL from the passed in String and perform the GET request
            try {
                URL tmdbUrl = new URL(tmdbQueryUrlString);
                return NetworkUtils.getResponseFromTmdbHttpUrl(tmdbUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(String data) {
            mTmdbJson = data;
            super.deliverResult(data);
        }
    }
}
