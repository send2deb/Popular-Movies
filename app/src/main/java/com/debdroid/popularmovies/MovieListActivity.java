package com.debdroid.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.debdroid.popularmovies.loaders.TmdbMovieDetailLoader;
import com.debdroid.popularmovies.model.Movie;
import com.debdroid.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieAdapter.MovieGAdapterOnClickHandler {

    private final static String LOG_TAG = MovieListActivity.class.getSimpleName();

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
        int spanCount = determineNumOfColumns();
        mMovieGridLayoutManager = new GridLayoutManager(this, spanCount,
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

        // Retrieve the sort type from SharedPreference. If nothing is present then use most popular
        // as default sort category
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mSortCategory = sharedPref.getString(getString(R.string.pref_user_sort_type_key),
                TMDB_POPULAR_MOVIE_CATEGORY);

        populateActionBarTitle(mSortCategory);

        makeMovieQuery();
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
                storeUserSortPreference(TMDB_POPULAR_MOVIE_CATEGORY);
                populateActionBarTitle(TMDB_POPULAR_MOVIE_CATEGORY);
                makeMovieQuery();
                return true;
            case R.id.menu_action_sort_highest_rated:
                mSortCategory = TMDB_TOP_RATED_MOVIE_CATEGORY;
                storeUserSortPreference(TMDB_TOP_RATED_MOVIE_CATEGORY);
                populateActionBarTitle(TMDB_TOP_RATED_MOVIE_CATEGORY);
                makeMovieQuery();
                return true;
            case R.id.menu_action_sort_user_favourite:
                mSortCategory = TMDB_USER_FAVOURITE_CATEGORY;
                storeUserSortPreference(TMDB_USER_FAVOURITE_CATEGORY);
                populateActionBarTitle(TMDB_USER_FAVOURITE_CATEGORY);
                makeMovieQuery();
                return true;
            default:
                Log.e(LOG_TAG, "Menu selection error");
        }
        return super.onOptionsItemSelected(item);
    }

    public void populateActionBarTitle(String type) {
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
     * This method constructs the URL (using {@link NetworkUtils}) for the TMDb endpoint and
     * starts the AsyncTaskLoader to perform the GET request.
     */
    private void makeMovieQuery() {
        // If it's a request for TMDb endpoint and the network connectivity is not available then
        // return with a Toast message
        if(!NetworkUtils.isOnline(this) && !mSortCategory.equals(TMDB_USER_FAVOURITE_CATEGORY)) {
            Toast.makeText(this, "No internet connection.",Toast.LENGTH_LONG).show();
            // Reset adapter. This is ensure no data is shown if user comes from favourite list to
            // other sort category
            mMovieAdapter.swapData(new ArrayList<Movie>());
            return;
        }

        // Create a bundle with the sort category to pass to Loader
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_QUERY_BUNDLE_EXTRA, mSortCategory);
//
//        if(mSortCategory == TMDB_USER_FAVOURITE_CATEGORY) {
//
//        }
//
//        // Get the URL for TMDb endpoint from NetworkUtils
//        URL tmdbMovieUrl = NetworkUtils.buildTmdbMovieListUrl(mSortCategory);


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
     * This method dynamically determines the number of column for the recycler view based on
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

        return new TmdbMovieDetailLoader(this, args);
//        return new LoadDataInBackground(this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {

        /* When we finish loading, hide the loading indicator from the user. */
        mProgressBar.setVisibility(View.INVISIBLE);
        /*
         * If the results are null, we assume an error has occurred. There are much more robust
         * methods for checking errors, but we wanted to keep this particular example simple.
         */
        if (data == null) {
            showErrorMessage();
        } else {
//            mMovieList = JsonUtils.parseTmdbMovieJson(data);
//            mMovieAdapter.swapData(mMovieList);
            resetErrorMEssage();
            mMovieList = data;
            mMovieAdapter.swapData(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        /* Do nothing */
    }

    private void showErrorMessage() {
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void resetErrorMEssage() {
        mErrorMessage.setVisibility(View.INVISIBLE);
    }

    /**
     * Override the interface method to interact with click even
     * @param position Position of the movie poster clicked
     */
    @Override
    public void onMovieItemClick(int position) {
        Intent startMovieDetailIntent = new Intent(this, MovieDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(MovieDetailActivity.MOVIE_ID_EXTRA_KEY, mMovieList.get(position)
                .getmMovieId());
        bundle.putString(MovieDetailActivity.MOVIE_TITLE_EXTRA_KEY, mMovieList.get(position)
                .getmOriginalTitle());
        bundle.putString(MovieDetailActivity.MOVIE_RELEASE_DATE_EXTRA_KEY, mMovieList.get(position)
                .getmReleaseDate());
        bundle.putString(MovieDetailActivity.MOVIE_POSTER_PATH_EXTRA_KEY, mMovieList.get(position)
                .getmPosterImage());
        bundle.putDouble(MovieDetailActivity.MOVIE_VOTE_AVERAGE_EXTRA_KEY, mMovieList.get(position)
                .getmUserRating());
        bundle.putString(MovieDetailActivity.MOVIE_PLOT_SYNOPSIS_EXTRA_KEY, mMovieList.get(position)
                .getmPlotSynopsis());
        bundle.putString(MovieDetailActivity.MOVIE_BACKDROP_PATH_EXTRA_KEY, mMovieList.get(position)
                .getmBackdropImage());
        startMovieDetailIntent.putExtras(bundle);
        startActivity(startMovieDetailIntent);
    }

//    /**
//     * This static inner class loads the data from Tmdb endpoint over internet in background thread
//     * using AsyncTaskLoader.
//     * Android Studio complained when used as anonymous class, so an inner static class is created.
//     */
//    private static class LoadDataInBackground extends AsyncTaskLoader<String> {
//
//        private final Bundle mBundle;
//
//        private LoadDataInBackground(Context context, Bundle bundle) {
//            super(context);
//            this.mBundle = bundle;
//        }
//
//        // Create a String member variable to store the raw JSON from TMDb endpoint
//        private String mTmdbJson;
//
//        @Override
//        protected void onStartLoading() {
//
//            // If no arguments were passed, nothing to do
//            if (mBundle == null) {
//                return;
//            }
//
//            // If mTmdbJson is not null, deliver that result. Otherwise, force a load
//            if(mTmdbJson != null) {
//                deliverResult(mTmdbJson);
//            } else {
//                forceLoad();
//            }
//        }
//
//        @Override
//        public String loadInBackground() {
//            // Extract the url from the args using the key
//            String tmdbQueryUrlString = mBundle.getString(MOVIE_QUERY_BUNDLE_EXTRA);
//
//            // Parse the URL from the passed in String and perform the GET request
//            try {
//                URL tmdbUrl = new URL(tmdbQueryUrlString);
//                return NetworkUtils.getResponseFromTmdbHttpUrl(tmdbUrl);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        public void deliverResult(String data) {
//            mTmdbJson = data;
//            super.deliverResult(data);
//        }
//    }
}
