package com.debdroid.popularmovies.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by debashispaul on 25/02/2018.
 */

public class PopularMovieProvider extends ContentProvider {
    private static final String TAG = "PopularMovieProvider";

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PopularMoviesDbHelper mOpenHelper;

    // Unique ids for Uri match
    private static final int POPULAR_MOVIE = 101;
    private static final int POPULAR_MOVIE_WITH_MOVIE_ID = 102;
    private static final int POPULAR_MOVIE_WITH_CATEGORY = 103;

    /**
     * Create a UriMatcher will all different types of Uris for popular_movies table
     *
     * @return The UriMatcher
     */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PopularMovieContract.CONTENT_AUTHORITY,
                PopularMovieContract.PATH_POPULAR_MOVIE, POPULAR_MOVIE);
        uriMatcher.addURI(PopularMovieContract.CONTENT_AUTHORITY,
                PopularMovieContract.PATH_POPULAR_MOVIE + "/#", POPULAR_MOVIE_WITH_MOVIE_ID);
        uriMatcher.addURI(PopularMovieContract.CONTENT_AUTHORITY,
                PopularMovieContract.PATH_POPULAR_MOVIE + "/*", POPULAR_MOVIE_WITH_CATEGORY);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final Cursor retCursor;
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case POPULAR_MOVIE:
                retCursor = mOpenHelper.getReadableDatabase().query
                        (PopularMovieContract.PopularMovies.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case POPULAR_MOVIE_WITH_MOVIE_ID:
                final String movieIdSelection = PopularMovieContract.PopularMovies.TABLE_NAME +
                        "." + PopularMovieContract.PopularMovies.COLUMN_MOVIE_ID + " = ?";
                final String movieId = Integer.toString(PopularMovieContract.PopularMovies
                        .getMovieIdFromUri(uri));
                final String[] movieIdSelectionArg = new String[]{movieId};
                retCursor = mOpenHelper.getReadableDatabase().query
                        (PopularMovieContract.PopularMovies.TABLE_NAME,
                                projection,
                                movieIdSelection,
                                movieIdSelectionArg,
                                null,
                                null,
                                sortOrder);
                break;
            case POPULAR_MOVIE_WITH_CATEGORY:
                final String movieCategorySelection =
                        PopularMovieContract.PopularMovies.TABLE_NAME +
                                "." + PopularMovieContract.PopularMovies.COLUMN_MOVIE_CATEGORY + " = ?";
                final String movieCategory = PopularMovieContract.PopularMovies
                        .getMovieCategoryFromMovieUri(uri);
                final String[] movieCategorySelectionArg = new String[]{movieCategory};
                retCursor = mOpenHelper.getReadableDatabase().query
                        (PopularMovieContract.PopularMovies.TABLE_NAME,
                                projection,
                                movieCategorySelection,
                                movieCategorySelectionArg,
                                null,
                                null,
                                sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Set the notification
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case POPULAR_MOVIE:
                return PopularMovieContract.PopularMovies.CONTENT_TYPE;
            case POPULAR_MOVIE_WITH_MOVIE_ID:
                return PopularMovieContract.PopularMovies.CONTENT_ITEM_TYPE;
            case POPULAR_MOVIE_WITH_CATEGORY:
                return PopularMovieContract.PopularMovies.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        final Uri returnUri;

        switch (match) {
            case POPULAR_MOVIE:
                final long _id = db.insert(PopularMovieContract.PopularMovies.TABLE_NAME,
                        null, values);
                if (_id > 0) {
                    returnUri = PopularMovieContract.PopularMovies.buildMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the change
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        final int deleteCount;
        // This makes delete all rows and returns the number of rows deleted
        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case POPULAR_MOVIE:
                deleteCount = db.delete(PopularMovieContract.PopularMovies.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the change if the delete count is greater than zero
        if (deleteCount != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        //Return the actual # of rows deleted
        return deleteCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        /* Not required for this project */
        return 0;
    }
}
