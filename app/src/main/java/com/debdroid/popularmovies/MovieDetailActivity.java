package com.debdroid.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.debdroid.popularmovies.utils.DateUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_TITLE_EXTRA_KEY = "movie_title";
    public static final String MOVIE_RELEASE_DATE_EXTRA_KEY = "movie_release_date";
    public static final String MOVIE_POSTER_PATH_EXTRA_KEY = "movie_poster_path";
    public static final String MOVIE_VOTE_AVERAGE_EXTRA_KEY = "movie_vote_average";
    public static final String MOVIE_PLOT_SYNOPSIS_EXTRA_KEY = "movie_plot_synopsis";
    public static final String MOVIE_BACKDROP_PATH_EXTRA_KEY = "movie_backdrop_path";

    private final static String TMDB_POSTER_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185";
    private final static String TMDB_BACKDROP_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w500";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ImageView mBackdropImage = findViewById(R.id.iv_backdrop_image);
        TextView mMovieTitle = findViewById(R.id.tv_movie_title);
        ImageView mMoviePoster = findViewById(R.id.iv_poster_image);
        TextView mVoteAverage = findViewById(R.id.tv_vote_average_data);
        TextView mReleaseDate = findViewById(R.id.tv_release_date_data);
        TextView mPlotSynopsis = findViewById(R.id.tv_plot_synopsis_data);

        // Get the bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            // Populate the UI fields
            String backdropImagePath = TMDB_BACKDROP_IMAGE_BASE_PATH
                    + bundle.getString(MOVIE_BACKDROP_PATH_EXTRA_KEY);
            Picasso.with(this).load(backdropImagePath).into(mBackdropImage);
            mMovieTitle.setText(bundle.getString(MOVIE_TITLE_EXTRA_KEY));
            String posterImagePath = TMDB_POSTER_IMAGE_BASE_PATH
                    + bundle.getString(MOVIE_POSTER_PATH_EXTRA_KEY);
            Picasso.with(this).load(posterImagePath).into(mMoviePoster);
            String friendlyDAte = DateUtils.formatFriendlyDate
                    (bundle.getString(MOVIE_RELEASE_DATE_EXTRA_KEY));
            mReleaseDate.setText(friendlyDAte);
            mVoteAverage.setText(Double.toString(bundle.getDouble(MOVIE_VOTE_AVERAGE_EXTRA_KEY)));
            mPlotSynopsis.setText(bundle.getString(MOVIE_PLOT_SYNOPSIS_EXTRA_KEY));
        }
    }
}
