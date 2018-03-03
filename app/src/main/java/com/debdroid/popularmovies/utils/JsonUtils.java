package com.debdroid.popularmovies.utils;

import android.util.Log;

import com.debdroid.popularmovies.model.Movie;
import com.debdroid.popularmovies.model.Review;
import com.debdroid.popularmovies.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final String TAG = "JsonUtils";

    private static final String JSON_RESULTS_KEY = "results";
    private static final String JSON_MOVIE_ID_KEY = "id";
    private static final String JSON_ORIGINAL_TITLE_KEY = "original_title";
    private static final String JSON_POSTER_PATH_KEY = "poster_path";
    private static final String JSON_OVERVIEW_KEY = "overview";
    private static final String JSON_VOTE_AVERAGE_KEY = "vote_average";
    private static final String JSON_RELEASE_DATE_KEY = "release_date";
    private static final String JSON_BACKDROP_PATH_KEY = "backdrop_path";
    private static final String JSON_VIDEOS_NAME_KEY = "name";
    private static final String JSON_VIDEOS_KEY_KEY = "key";
    private static final String JSON_REVIEWS_AUTHOR_KEY = "author";
    private static final String JSON_REVIEWS_CONTENT_KEY = "content";

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

            if (movieListObject.has(JSON_RESULTS_KEY)) {
                JSONArray movieListAsArray = movieListObject.optJSONArray(JSON_RESULTS_KEY);
                for (int i = 0; i < movieListAsArray.length(); i++) {
                    Movie movie = new Movie();

                    JSONObject movieObject = movieListAsArray.optJSONObject(i);

                    if (movieObject.has(JSON_MOVIE_ID_KEY)) {
                        movie.setmMovieId(movieObject.optInt(JSON_MOVIE_ID_KEY));
                    }
                    if (movieObject.has(JSON_ORIGINAL_TITLE_KEY)) {
                        movie.setmOriginalTitle(movieObject.optString(JSON_ORIGINAL_TITLE_KEY));
                    }
                    if (movieObject.has(JSON_POSTER_PATH_KEY)) {
                        movie.setmPosterImage(movieObject.optString(JSON_POSTER_PATH_KEY));
                    }
                    if (movieObject.has(JSON_OVERVIEW_KEY)) {
                        movie.setmPlotSynopsis(movieObject.optString(JSON_OVERVIEW_KEY));
                    }
                    if (movieObject.has(JSON_VOTE_AVERAGE_KEY)) {
                        movie.setmUserRating(movieObject.optDouble(JSON_VOTE_AVERAGE_KEY));
                    }
                    if (movieObject.has(JSON_RELEASE_DATE_KEY)) {
                        movie.setmReleaseDate(movieObject.optString(JSON_RELEASE_DATE_KEY));
                    }
                    if (movieObject.has(JSON_BACKDROP_PATH_KEY)) {
                        movie.setmBackdropImage(movieObject.optString(JSON_BACKDROP_PATH_KEY));
                    }

                    movieList.add(movie);
                }
            } else {
                Log.e(TAG, "Invalid Json movie data");
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json movie data parsing error");
            e.printStackTrace();
        }

        return movieList;
    }

    /**
     * Parse the videos (i.e. Trailer) of a movie Json of Tmdb endpoint
     *
     * @param json The TMDb movie video json data that will be parsed.
     * @return The parsed trailer list.
     */
    public static List<Video> parseTmdbMovieVideoJson(String json) {

        List<Video> videoList = new ArrayList<>();

        try {
            JSONObject videoListObject = new JSONObject(json);

            if (videoListObject.has(JSON_RESULTS_KEY)) {
                JSONArray videoListAsArray = videoListObject.optJSONArray(JSON_RESULTS_KEY);
                for (int i = 0; i < videoListAsArray.length(); i++) {
                    Video video = new Video();

                    JSONObject videoObject = videoListAsArray.optJSONObject(i);

                    if (videoObject.has(JSON_VIDEOS_NAME_KEY)) {
                        video.setmName(videoObject.optString(JSON_VIDEOS_NAME_KEY));
                    }
                    if (videoObject.has(JSON_VIDEOS_KEY_KEY)) {
                        video.setmKey(videoObject.optString(JSON_VIDEOS_KEY_KEY));
                    }
                    videoList.add(video);
                }
            } else {
                Log.e(TAG, "Invalid Json video data");
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json video data parsing error");
            e.printStackTrace();
        }

        return videoList;
    }

    /**
     * Parse the reviews of a movie Json of Tmdb endpoint
     *
     * @param json The TMDb movie review json data that will be parsed.
     * @return The parsed review list.
     */
    public static List<Review> parseTmdbMovieReviewJson(String json) {

        List<Review> reviewList = new ArrayList<>();

        try {
            JSONObject reviewListObject = new JSONObject(json);

            if (reviewListObject.has(JSON_RESULTS_KEY)) {
                JSONArray reviewListAsArray = reviewListObject.optJSONArray(JSON_RESULTS_KEY);
                for (int i = 0; i < reviewListAsArray.length(); i++) {
                    Review review = new Review();

                    JSONObject reviewObject = reviewListAsArray.optJSONObject(i);

                    if (reviewObject.has(JSON_REVIEWS_AUTHOR_KEY)) {
                        review.setmAuthor(reviewObject.optString(JSON_REVIEWS_AUTHOR_KEY));
                    }
                    if (reviewObject.has(JSON_REVIEWS_CONTENT_KEY)) {
                        // Relace all blank lines, new lines and tabs
                        String content = reviewObject.optString(JSON_REVIEWS_CONTENT_KEY)
                                .replaceAll("(?m)^[ \t]*\r?\n", "");
                        review.setmContent(content);
                    }
                    reviewList.add(review);
                }
            } else {
                Log.e(TAG, "Invalid Json review data");
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json review data parsing error");
            e.printStackTrace();
        }

        return reviewList;
    }
}
