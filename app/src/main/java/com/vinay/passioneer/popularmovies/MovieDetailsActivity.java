package com.vinay.passioneer.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(savedInstanceState == null) {

            Bundle bundle = getIntent().getExtras();


            Bundle argBundle = new Bundle();
            argBundle.putParcelable("movieDetails",bundle.getParcelable("movieDetails"));
            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setArguments(argBundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container,movieDetailsFragment)
                    .commit();
        }
    }

}
