package com.vinay.passioneer.popularmovies;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements PopularMoviesFragment.TMDB_Callback, MovieDetailsFragment.ReloadCallback {

    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "TMDB_DETAIL_TAG";
    private boolean mTwoPane;
    private String mSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Stetho.initializeWithDefaults(this);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            Util.isMultiPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailsFragment(), MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }

        } else {
            Util.isMultiPane = false;
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String sortBy = Util.getPreferredSortByOption(this);
        String favourite = getString(R.string.pref_sortBy_favourite_value);
        if (sortBy != null && !sortBy.equals(mSortBy)) {
            PopularMoviesFragment popularMoviesFragment = (PopularMoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_popular_movies);
            if (null != popularMoviesFragment) {
                if (!favourite.equals(sortBy)) {
                    // no need to call API since user has selected favourite option
                    if (sortBy.equals(getString(R.string.pref_sortBy_highestRated_value))) {
                        getSupportActionBar().setTitle(R.string.tile_highest_rated_movies);
                    } else {
                        getSupportActionBar().setTitle(R.string.tile_popular_movies);
                    }
                    popularMoviesFragment.updatePopularMovies(sortBy);
                } else {
                    //View view = LayoutInflater.from(this).inflate(R.layout.fragment_main, null);
                    //popularMoviesFragment.updateFavouriteMovies(view);
                    getSupportActionBar().setTitle(R.string.tile_favourite_movies);
                    popularMoviesFragment.showFavouriteMovies();
                }
            }
            mSortBy = sortBy;
        }
    }

    @Override
    public void onItemSelected(Bundle bundle) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.


            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent movieDetailsIntent = new Intent(this, MovieDetailsActivity.class);
            movieDetailsIntent.putExtras(bundle);
            startActivity(movieDetailsIntent);
        }
    }

    @Override
    public void deleteItem() {
        PopularMoviesFragment popularMoviesFragment =
                (PopularMoviesFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_popular_movies);

        popularMoviesFragment.restartLoader();

        if(Util.counter == 1) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new MovieDetailsFragment(), MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
            FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.favouriteFAB);
            floatingActionButton.setClickable(false);
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.MAGENTA));
        }

    }
}
