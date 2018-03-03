package com.debdroid.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.debdroid.popularmovies.R;
import com.debdroid.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debashispaul on 21/02/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String TAG = "MovieAdapter";

    private final MovieGAdapterOnClickHandler mMovieGAdapterOnClickHandler;

    private static final String TMDB_POSTER_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185";
    private final Context mContext;
    private List<Movie> mMovieList;


    public MovieAdapter(Context context, MovieGAdapterOnClickHandler clickHandler) {
        Log.d(TAG, "Movie Adapter constructor");
        mContext = context;
        mMovieGAdapterOnClickHandler = clickHandler;
        mMovieList = new ArrayList<>();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mMoviePosterImageView;
        private final TextView mMovieOriginalTitle;

        private MovieViewHolder(final View view) {
            super(view);
            mMoviePosterImageView = view.findViewById(R.id.iv_single_movie_poster_image);
            mMovieOriginalTitle = view.findViewById(R.id.tv_single_movie_original_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mMovieGAdapterOnClickHandler.onMovieItemClick(adapterPosition, this);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_movie_item,
                parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.mMovieOriginalTitle.setText(mMovieList.get(position).getmOriginalTitle());
        String posterImagePath = TMDB_POSTER_IMAGE_BASE_PATH
                + mMovieList.get(position).getmPosterImage();
        Picasso.with(mContext).load(posterImagePath).into(holder.mMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieList.isEmpty()) {
            return 0;
        } else {
            return mMovieList.size();
        }
    }

    public void swapData(List<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

    /**
     * This is the interface which will be implemented by MovieListActivity
     */
    public interface MovieGAdapterOnClickHandler {
        void onMovieItemClick(int position, MovieAdapter.MovieViewHolder vh);
    }
}
