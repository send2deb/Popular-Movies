package com.debdroid.popularmovies.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.debdroid.popularmovies.MovieListActivity;
import com.debdroid.popularmovies.contentprovider.PopularMovieContract;
import com.debdroid.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debashispaul on 25/02/2018.
 */

public class DatabaseUtils {


    public static boolean isUserFavouriteMovie(Context context, int movieId) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri movieIdUri = PopularMovieContract.PopularMovies.buildMovieUriWithMovieId(movieId);

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(movieIdUri, null, null,
                    null, null);

            if (cursor.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Uri insertUserFavouriteMovie(Context context, Movie movie) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri movieUri = PopularMovieContract.PopularMovies.CONTENT_URI;

        ContentValues contentValues = new ContentValues();
        contentValues.put(PopularMovieContract.PopularMovies.COLUMN_MOVIE_ID, movie.getmMovieId());
        contentValues.put(PopularMovieContract.PopularMovies.COLUMN_MOVIE_TITLE,
                movie.getmOriginalTitle());
        contentValues.put(PopularMovieContract.PopularMovies.COLUMN_MOVIE_CATEGORY,
                MovieListActivity.TMDB_USER_FAVOURITE_CATEGORY);
        contentValues.put(PopularMovieContract.PopularMovies.COLUMN_MOVIE_POSTER_PATH,
                movie.getmPosterImage());
        contentValues.put(PopularMovieContract.PopularMovies.COLUMN_MOVIE_RELEASE_DATE,
                movie.getmReleaseDate());
        contentValues.put(PopularMovieContract.PopularMovies.COLUMN_MOVIE_VOTE_AVERAGE,
                movie.getmUserRating());
        contentValues.put(PopularMovieContract.PopularMovies.COLUMN_MOVIE_BACKDROP_PATH,
                movie.getmBackdropImage());
        contentValues.put(PopularMovieContract.PopularMovies.COLUMN_MOVIE_PLOT_SYNOPSIS,
                movie.getmPlotSynopsis());

        return contentResolver.insert(movieUri, contentValues);
    }

    public static void deleteUserFavouriteMovie(Context context, int movieId) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri movieUri = PopularMovieContract.PopularMovies.CONTENT_URI;

        String selection = PopularMovieContract.PopularMovies.COLUMN_MOVIE_ID + " ?";
        String[] selectionArgs = new String[] {Integer.toString(movieId)};

        contentResolver.delete(movieUri, selection, selectionArgs);
    }

    public static List<Movie> getUserFavouriteMovie(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri movieUri = PopularMovieContract.PopularMovies.CONTENT_URI;

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(movieUri, null, null, null,
                    PopularMovieContract.PopularMovies.COLUMN_MOVIE_ID + " ASC");
            if (cursor.moveToFirst()) {
                return createListFromCursor(cursor);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static List<Movie> createListFromCursor(Cursor cursor) {
        List<Movie> movieList = new ArrayList<>();
        do {
            Movie movie = new Movie();
            movie.setmMovieId(cursor.getInt(cursor
                    .getColumnIndex(PopularMovieContract.PopularMovies.COLUMN_MOVIE_ID)));
            movie.setmOriginalTitle(cursor.getString(cursor
                    .getColumnIndex(PopularMovieContract.PopularMovies.COLUMN_MOVIE_TITLE)));
            movie.setmPosterImage(cursor.getString(cursor
                    .getColumnIndex(PopularMovieContract.PopularMovies.COLUMN_MOVIE_POSTER_PATH)));
            movie.setmReleaseDate(cursor.getString(cursor
                    .getColumnIndex(PopularMovieContract.PopularMovies.COLUMN_MOVIE_RELEASE_DATE)));
            movie.setmUserRating(cursor.getDouble(cursor
                    .getColumnIndex(PopularMovieContract.PopularMovies.COLUMN_MOVIE_VOTE_AVERAGE)));
            movie.setmBackdropImage(cursor.getString(cursor
                    .getColumnIndex(PopularMovieContract.PopularMovies.COLUMN_MOVIE_BACKDROP_PATH)));
            movie.setmPlotSynopsis(cursor.getString(cursor
                    .getColumnIndex(PopularMovieContract.PopularMovies.COLUMN_MOVIE_PLOT_SYNOPSIS)));
            movieList.add(movie);
        } while(cursor.moveToNext());
        return movieList;
    }
}
