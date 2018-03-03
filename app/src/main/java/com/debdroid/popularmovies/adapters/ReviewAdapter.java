package com.debdroid.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.debdroid.popularmovies.R;
import com.debdroid.popularmovies.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debashispaul on 02/03/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VideoViewHolder> {
    private static final String TAG = "ReviewAdapter";

    private final ReviewAdapterOnClickHandler mReviewAdapterOnClickHandler;
    private List<Review> mReviewList;


    public ReviewAdapter(ReviewAdapterOnClickHandler clickHandler) {
        Log.d(TAG, "Review Adapter constructor");
        mReviewAdapterOnClickHandler = clickHandler;
        mReviewList = new ArrayList<>();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mReviewAuthorTextView;
        private final TextView mReviewContentTextView;

        private VideoViewHolder(final View view) {
            super(view);
            mReviewAuthorTextView = view.findViewById(R.id.tv_review_author_data);
            mReviewContentTextView = view.findViewById(R.id.tv_review_content_data);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mReviewAdapterOnClickHandler.onReviewItemClick(adapterPosition);
        }
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_review_item,
                parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.mReviewAuthorTextView.setText(mReviewList.get(position).getmAuthor());
        holder.mReviewContentTextView.setText(mReviewList.get(position).getmContent());
    }

    @Override
    public int getItemCount() {
        if (mReviewList.isEmpty()) {
            return 0;
        } else {
            return mReviewList.size();
        }
    }

    public void swapData(List<Review> reviewList) {
        mReviewList = reviewList;
        notifyDataSetChanged();
    }

    /**
     * This is the interface which will be implemented by MovieDetailActivity
     */
    public interface ReviewAdapterOnClickHandler {
        void onReviewItemClick(int position);
    }
}