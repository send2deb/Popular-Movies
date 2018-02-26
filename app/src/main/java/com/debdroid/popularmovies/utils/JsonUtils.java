package com.debdroid.popularmovies.utils;

import android.util.Log;

import com.debdroid.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private final static String LOG_TAG = JsonUtils.class.getSimpleName();

    private static final String JSON_RESULTS_KEY = "results";
    private static final String JSON_MOVIE_ID_KEY = "id";
    private static final String JSON_ORIGINAL_TITLE_KEY = "original_title";
    private static final String JSON_POSTER_PATH_KEY = "poster_path";
    private static final String JSON_OVERVIEW_KEY = "overview";
    private static final String JSON_VOTE_AVERAGE_KEY = "vote_average";
    private static final String JSON_RELEASE_DATE_KEY = "release_date";
    private static final String JSON_BACKDROP_PATH_KEY = "backdrop_path";

    /**
     * Parse the movie list Json of Tmdb endpoint
     *
     * @param json The TMDb json data that will be parsed.
     * @return The parsed movie list.
     */

    public static List<Movie> parseTmdbMovieJson(String json) {

        List<Movie> movieList = new ArrayList<>();

        try {
            JSONObject movieListObject = new JSONObject(json);

            if(movieListObject.has(JSON_RESULTS_KEY)) {
                JSONArray movieListAsArray = movieListObject.optJSONArray(JSON_RESULTS_KEY);
                for (int i = 0; i < movieListAsArray.length(); i++) {
                    Movie movie = new Movie();

                    JSONObject movieObject = movieListAsArray.optJSONObject(i);

                    if(movieObject.has(JSON_MOVIE_ID_KEY)) {
                        movie.setmMovieId(movieObject.optInt(JSON_MOVIE_ID_KEY));
                    }
                    if(movieObject.has(JSON_ORIGINAL_TITLE_KEY)) {
                        movie.setmOriginalTitle(movieObject.optString(JSON_ORIGINAL_TITLE_KEY));
                    }
                    if(movieObject.has(JSON_POSTER_PATH_KEY)) {
                        movie.setmPosterImage(movieObject.optString(JSON_POSTER_PATH_KEY));
                    }
                    if(movieObject.has(JSON_OVERVIEW_KEY)) {
                        movie.setmPlotSynopsis(movieObject.optString(JSON_OVERVIEW_KEY));
                    }
                    if(movieObject.has(JSON_VOTE_AVERAGE_KEY)) {
                        movie.setmUserRating(movieObject.optDouble(JSON_VOTE_AVERAGE_KEY));
                    }
                    if(movieObject.has(JSON_RELEASE_DATE_KEY)) {
                        movie.setmReleaseDate(movieObject.optString(JSON_RELEASE_DATE_KEY));
                    }
                    if(movieObject.has(JSON_BACKDROP_PATH_KEY)) {
                        movie.setmBackdropImage(movieObject.optString(JSON_BACKDROP_PATH_KEY));
                    }

                    movieList.add(movie);
                }
            } else {
                Log.e(LOG_TAG,"Invalid Json data");
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Json parsing error");
            e.printStackTrace();
        }

        return movieList;
    }
}
