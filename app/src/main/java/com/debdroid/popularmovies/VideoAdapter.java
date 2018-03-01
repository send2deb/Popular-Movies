package com.debdroid.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.debdroid.popularmovies.model.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debashispaul on 28/02/2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private static final String LOG_TAG = VideoAdapter.class.getSimpleName();

    private final VideoAdapterOnClickHandler mVideoAdapterOnClickHandler;
    private List<Video> mVideoList;


    public VideoAdapter(VideoAdapterOnClickHandler clickHandler) {
        Log.d(LOG_TAG, "Video Adapter constructor");
        mVideoAdapterOnClickHandler = clickHandler;
        mVideoList = new ArrayList<>();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageButton mVideoPlayImageButton;
        private final TextView mVideoTrailerCountTextView;

        private VideoViewHolder(final View view) {
            super(view);
            mVideoPlayImageButton = view.findViewById(R.id.ib_video_trailer_play_button);
            mVideoTrailerCountTextView = view.findViewById(R.id.tv_video_number_data);
            mVideoPlayImageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mVideoAdapterOnClickHandler.onTrailerItemClick(adapterPosition);
        }
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_item,
                parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.mVideoTrailerCountTextView.setText(Integer.toString(position + 1));
    }

    @Override
    public int getItemCount() {
        if(mVideoList.isEmpty()) {
            return 0;
        } else {
            return mVideoList.size();
        }
    }

    public void swapData(List<Video> videoList) {
        mVideoList = videoList;
        notifyDataSetChanged();
    }

    /**
     * This is the interface which will be implemented by MovieDetail Activity
     */
    public interface VideoAdapterOnClickHandler {
        void onTrailerItemClick(int position);
    }
}
