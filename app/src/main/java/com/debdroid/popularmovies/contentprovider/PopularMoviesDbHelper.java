package com.debdroid.popularmovies.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by debashispaul on 25/02/2018.
 */

public class PopularMoviesDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "PopularMoviesDbHelper";

    private static final int POPULAR_MOVIE_DATABASE_VERSION = 1;
    private static final String POPULAR_MOVIE_DATABASE_NAME = "popular_movie.db";

    public PopularMoviesDbHelper(Context context) {
        super(context, POPULAR_MOVIE_DATABASE_NAME, null, POPULAR_MOVIE_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create the SQL to create movie_basic_info table
        final  String SQL_CREATE_POPULAR_MOVIE_TABLE = "CREATE TABLE " +
                PopularMovieContract.PopularMovies.TABLE_NAME + " (" +
                PopularMovieContract.PopularMovies._ID + " INTEGER PRIMARY KEY," +
                PopularMovieContract.PopularMovies.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                PopularMovieContract.PopularMovies.COLUMN_MOVIE_CATEGORY + " TEXT NOT NULL," +
                PopularMovieContract.PopularMovies.COLUMN_MOVIE_TITLE + " TEXT NOT NULL," +
                PopularMovieContract.PopularMovies.COLUMN_MOVIE_POSTER_PATH + " TEXT NULL," +
                PopularMovieContract.PopularMovies.COLUMN_MOVIE_RELEASE_DATE+ " TEXT NULL," +
                PopularMovieContract.PopularMovies.COLUMN_MOVIE_VOTE_AVERAGE + " REAL DEFAULT 0.0," +
                PopularMovieContract.PopularMovies.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NULL," +
                PopularMovieContract.PopularMovies.COLUMN_MOVIE_PLOT_SYNOPSIS + " TEXT NULL," +
                "UNIQUE (" + PopularMovieContract.PopularMovies.COLUMN_MOVIE_ID + "," +
                PopularMovieContract.PopularMovies.COLUMN_MOVIE_CATEGORY + ") ON CONFLICT REPLACE);";

        //Now create the table
        db.execSQL(SQL_CREATE_POPULAR_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() called with: db = [" + db + "], oldVersion = [" + oldVersion +
                "], newVersion = [" + newVersion + "]");
        //Drop all the tables
        db.execSQL("DROP TABLE IF EXISTS " + PopularMovieContract.PopularMovies.TABLE_NAME);

        //Call onCreate to re-create the tables
        onCreate(db);
    }
}
