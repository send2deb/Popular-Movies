package com.debdroid.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.debdroid.popularmovies.adapters.MovieAdapter;
import com.debdroid.popularmovies.loaders.MovieListLoader;
import com.debdroid.popularmovies.model.Movie;
import com.debdroid.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieAdapter.MovieGAdapterOnClickHandler {
    private static final String TAG = "MovieListActivity";

    /* A constant to save and restore the URL that is being displayed */
    public static final String MOVIE_QUERY_BUNDLE_EXTRA = "query";

    /* Unique identifier for Loader */
    private static final int TMDB_MOVIES_LOADER = 84;

    /* TMDb category for popular movie */
    private static final String TMDB_POPULAR_MOVIE_CATEGORY = "popular";

    /* TMDb category for popular movie */
    private static final String TMDB_TOP_RATED_MOVIE_CATEGORY = "top_rated";

    /* TMDb category for user favourite movie */
    public static final String TMDB_USER_FAVOURITE_CATEGORY = "user_favourite";

    /* Key to save and restore layout manager's state */
    private static final String LAYOUT_MANGER_STATE_KEY = "layout_manager_state_key";
    /* Define a Parcelable to store the state of GridLayoutManager */
    private Parcelable mGridLayoutManagerState;

    /* Bundle key for Movie Parcelable */
    public static final String MOVIE_PARCELABLE_EXTRA_KEY = "movie_parcelable_extra_key";

    /* Define UI fields */
    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    
    /* Define the adapter */
    private MovieAdapter mMovieAdapter;

    /* All other member fields */
    private List<Movie> mMovieList = new ArrayList<>();
    private String mSortCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: is called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        mErrorMessageTextView = findViewById(R.id.tv_error_message_display);
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        
        mRecyclerView = findViewById(R.id.rv_movie_list);
        mMovieAdapter = new MovieAdapter(this, this);
        int spanCount = determineNumOfColumns();
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount,
                GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //Set this to false for smooth scrolling of RecyclerView
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

        // Retrieve the sort type from SharedPreference. If nothing is present then use most popular
        // as default sort category
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mSortCategory = sharedPref.getString(getString(R.string.pref_user_sort_type_key),
                TMDB_POPULAR_MOVIE_CATEGORY);

        populateActionBarTitle(mSortCategory);

        startMovieQuery();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // This is to ensure that the favourite movie list grid is refreshed whenever user removes
        // a favourite movie from detail page and comes back to list
        startMovieQuery();
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
            // It's the top level menu, no action required
            case R.id.menu_action_sort:
                // It's the top level menu, no action required
                return true;
            case R.id.menu_action_sort_most_popular:
                mSortCategory = TMDB_POPULAR_MOVIE_CATEGORY;
                storeUserSortPreference(TMDB_POPULAR_MOVIE_CATEGORY);
                populateActionBarTitle(TMDB_POPULAR_MOVIE_CATEGORY);
                startMovieQuery();
                return true;
            case R.id.menu_action_sort_highest_rated:
                mSortCategory = TMDB_TOP_RATED_MOVIE_CATEGORY;
                storeUserSortPreference(TMDB_TOP_RATED_MOVIE_CATEGORY);
                populateActionBarTitle(TMDB_TOP_RATED_MOVIE_CATEGORY);
                startMovieQuery();
                return true;
            case R.id.menu_action_sort_user_favourite:
                mSortCategory = TMDB_USER_FAVOURITE_CATEGORY;
                storeUserSortPreference(TMDB_USER_FAVOURITE_CATEGORY);
                populateActionBarTitle(TMDB_USER_FAVOURITE_CATEGORY);
                startMovieQuery();
                return true;
            default:
                Log.e(TAG, "Menu selection error");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mGridLayoutManagerState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(LAYOUT_MANGER_STATE_KEY, mGridLayoutManagerState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            mGridLayoutManagerState = savedInstanceState.getParcelable(LAYOUT_MANGER_STATE_KEY);
        }
    }

    /**
     * This method sets the title of the activity based on the movie category
     * @param type The movie category
     */
    private void populateActionBarTitle(String type) {
        String title;
        switch (type) {
            case TMDB_POPULAR_MOVIE_CATEGORY:
                title = getResources().getString(R.string.menu_action_sort_most_popular);
                break;
            case TMDB_TOP_RATED_MOVIE_CATEGORY:
                title = getResources().getString(R.string.menu_action_sort_highest_rated);
                break;
            case TMDB_USER_FAVOURITE_CATEGORY:
                title = getResources().getString(R.string.menu_action_sort_user_favourite);
                break;
            default:
                title = getResources().getString(R.string.app_name);
        }
        setTitle(title);
    }

    /**
     * This method starts the loader to load the movie. It also checks if the network connection 
     * is available before TMDb endpoint data loading.
     */
    private void startMovieQuery() {
        // If it's a request for TMDb endpoint and the network connectivity is not available then
        // return with a Toast message
        if(!NetworkUtils.isOnline(this) && !mSortCategory.equals(TMDB_USER_FAVOURITE_CATEGORY)) {
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection_msg),
                    Toast.LENGTH_LONG).show();
            // Reset adapter. This ensures no data is shown if user comes from favourite list to
            // other sort category
            mMovieAdapter.swapData(new ArrayList<Movie>());
            showErrorMessage();
            return;
        } else {
            resetErrorMessage();
        }

        // Create a bundle with the sort category to pass to Loader
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_QUERY_BUNDLE_EXTRA, mSortCategory);


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


    /**
     * This method writes the user sort preference to SharedPreference file
     * @param sortCategory The sort category selected by the user
     */
    private void storeUserSortPreference(String sortCategory) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_user_sort_type_key),sortCategory);
        editor.apply(); // Use apply to writhe the value asynchronously
    }

    /**
     * This method dynamically determines the number of column for the RecyclerView based on
     * screen width and density
     * @return number of calculated columns
     */
    private int determineNumOfColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int columnWidth = (int) (getResources().getDimension(R.dimen.single_movie_image_width) /
                        displayMetrics.density);
        int numOfColumn = (int) dpWidth / columnWidth;
        return numOfColumn;
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {

        // Show the loading indicator only when it's online and trying to load the data
        if(NetworkUtils.isOnline(this)) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        return new MovieListLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {

        /* When we finish loading, hide the loading indicator from the user. */
        mProgressBar.setVisibility(View.INVISIBLE);
        /*
         * If the results are null, then some error occurred
         */
        if (data == null) {
            // Reset adapter
            mMovieAdapter.swapData(new ArrayList<Movie>());
            showErrorMessage();
        } else {
            resetErrorMessage();
            mMovieList = data;
            mMovieAdapter.swapData(data);
            // Reposition the RecyclerView in case of orientation change
            if(mGridLayoutManagerState != null) {
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mGridLayoutManagerState);
                // Reset it to null to ensure RecyclerView positions zero if category changed in between
                mGridLayoutManagerState = null;
            } else { // Position to zero if the category changed
                mRecyclerView.scrollToPosition(0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        /* Do nothing */
    }

    /**
     * This method displays the appropriate error message
     */
    private void showErrorMessage() {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        if(mSortCategory.equals(TMDB_USER_FAVOURITE_CATEGORY)) {
            mErrorMessageTextView.setText(getResources().getString(R.string.error_msg_user_favourite));
        } else {
            mErrorMessageTextView.setText(getResources().getString(R.string.error_msg_tmdb));
        }
    }

    /**
     * This method resets the error message
     */
    private void resetErrorMessage() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * Override the interface method to interact with click even
     * @param position Position of the movie poster clicked
     */
    @Override
    public void onMovieItemClick(int position, MovieAdapter.MovieViewHolder vh) {
        Intent startMovieDetailIntent = new Intent(this, MovieDetailActivity.class);
        startMovieDetailIntent.putExtra(MOVIE_PARCELABLE_EXTRA_KEY, mMovieList.get(position));
        ImageView imageView = vh.mMoviePosterImageView;
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, imageView,
                        ViewCompat.getTransitionName(imageView));
        startActivity(startMovieDetailIntent, options.toBundle());
    }
}
