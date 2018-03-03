package com.debdroid.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            TextView authorTextView = findViewById(R.id.tv_full_review_author_data);
            TextView contentTextView = findViewById(R.id.tv_full_review_content_data);
            authorTextView.setText(bundle
                    .getString(MovieDetailActivity.MOVIE_REVIEW_AUTHOR_BUNDLE_EXTRA));
            contentTextView.setText(bundle
                    .getString(MovieDetailActivity.MOVIE_REVIEW_CONTENT_BUNDLE_EXTRA));
            setTitle(bundle.getString(MovieDetailActivity.MOVIE_REVIEW_TITLE_BUNDLE_EXTRA));
        }
    }
}
