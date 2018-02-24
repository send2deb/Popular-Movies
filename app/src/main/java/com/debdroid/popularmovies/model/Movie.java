package com.debdroid.popularmovies.model;

/**
 * Created by debashispaul on 21/02/2018.
 */

public class Movie {
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
}
