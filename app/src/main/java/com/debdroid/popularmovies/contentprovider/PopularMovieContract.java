package com.debdroid.popularmovies.contentprovider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by debashispaul on 25/02/2018.
 */

public final class PopularMovieContract {
    private static final String TAG = "PopularMovieContract";

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.debdroid.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Paths (appended to base content URI for possible URI's) for the movies table
    public static final String PATH_POPULAR_MOVIE = "popular_movies";

    /**
     Inner class that extends BaseColumns and defines the table contents of the popular_movies table
     */
    public static final class PopularMovies implements BaseColumns {

        // Table name for Popular Movies
        public static final String TABLE_NAME = PATH_POPULAR_MOVIE;

        // Column to store movie id
        public static final String COLUMN_MOVIE_ID = "movie_id";
        // Column to store movie title
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        // Column to store movie category (i.e. user favourite)
        public static final String COLUMN_MOVIE_CATEGORY = "movie_category";
        // Column to store movie poster path
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
        // Column to store movie release date
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        // Column to store movie user rating
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "movie_vote_average";
        // Column to store movie backdrop path
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "movie_backdrop_path";
        // Column to store movie plot synopsis
        public static final String COLUMN_MOVIE_PLOT_SYNOPSIS = "movie_plot_synopsis";

        // Uri for popular_movies table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR_MOVIE).build();

        // Content types
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_POPULAR_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_POPULAR_MOVIE;

        /**
         * Build the popular movie item uri with the row id
         * @param id The row id
         * @return The Uri with row id appended as path
         */
        public static Uri buildMovieUri(final long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Build the uri with movie id appended as path
         * @param movieId The movie id
         * @return The full uri of the movie item
         */
        public static Uri buildMovieUriWithMovieId(int movieId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movieId)).build();
        }

        /**
         * Build the uri with movie category (i.e. user favourite) appended as path
         * @param movieCategory The category (i.e. user favourite) of the movies
         * @return The full uri of the movie category
         */
        public static Uri buildMovieUriWithMovieCategory (String movieCategory) {
            return CONTENT_URI.buildUpon().appendPath(movieCategory).build();
        }

        /**
         * Retrieve the movie id from the uri
         * @param uri The uri of the movie item
         * @return The movie id
         */
        public static int getMovieIdFromUri (Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

        /**
         * Retrieve the movie category (i.e. user favourite) from the uri
         * @param uri The uri of the movie category
         * @return The movie category (i.e. user favourite )
         */
        static String getMovieCategoryFromMovieUri (Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
