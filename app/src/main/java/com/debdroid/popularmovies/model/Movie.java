package com.debdroid.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by debashispaul on 21/02/2018.
 */

public class Movie implements Parcelable {
    private int mMovieId;
    private String mOriginalTitle;
    private String mPosterImage;
    private String mPlotSynopsis;
    private double mUserRating;
    private String mReleaseDate;
    private String mBackdropImage;

    /* No argument constructor with default values */
    public Movie() {
        this.mOriginalTitle = null;
        this.mPosterImage = null;
        this.mPlotSynopsis = null;
        this.mUserRating = 0.0;
        this.mBackdropImage = null;
    }

    public Movie(String mOriginalTitle, String mPosterImage, String mPlotSynopsis,
                 float mUserRating, String mBackdropImage) {
        this.mOriginalTitle = mOriginalTitle;
        this.mPosterImage = mPosterImage;
        this.mPlotSynopsis = mPlotSynopsis;
        this.mUserRating = mUserRating;
        this.mBackdropImage = mBackdropImage;
    }

    public int getmMovieId() {
        return mMovieId;
    }

    public void setmMovieId(int mMovieId) {
        this.mMovieId = mMovieId;
    }

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public void setmOriginalTitle(String mOriginalTitle) {
        this.mOriginalTitle = mOriginalTitle;
    }

    public String getmPosterImage() {
        return mPosterImage;
    }

    public void setmPosterImage(String mPosterImage) {
        this.mPosterImage = mPosterImage;
    }

    public String getmPlotSynopsis() {
        return mPlotSynopsis;
    }

    public void setmPlotSynopsis(String mPlotSynopsis) {
        this.mPlotSynopsis = mPlotSynopsis;
    }

    public double getmUserRating() {
        return mUserRating;
    }

    public void setmUserRating(double mUserRating) {
        this.mUserRating = mUserRating;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getmBackdropImage() {
        return mBackdropImage;
    }

    public void setmBackdropImage(String mBackdropImage) {
        this.mBackdropImage = mBackdropImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mMovieId);
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterImage);
        dest.writeString(mPlotSynopsis);
        dest.writeDouble(mUserRating);
        dest.writeString(mReleaseDate);
        dest.writeString(mBackdropImage);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        mMovieId = in.readInt();
        mOriginalTitle = in.readString();
        mPosterImage = in.readString();
        mPlotSynopsis = in.readString();
        mUserRating = in.readDouble();
        mReleaseDate = in.readString();
        mBackdropImage = in.readString();
    }
}
