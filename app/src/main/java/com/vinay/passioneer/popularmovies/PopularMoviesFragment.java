package com.vinay.passioneer.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vinay.passioneer.popularmovies.model.MovieModel;
import com.vinay.passioneer.popularmovies.services.TMDB_Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PopularMoviesFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private ArrayList<MovieModel> movieModelList;
    private static final String LOG_TAG="PopularMoviesFragment";

    public PopularMoviesFragment() {
    }

    @Override
    public void onStart() {
            super.onStart();
            updatePopularMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG,"Saving Instance");
        outState.putParcelableArrayList("popularMovies",movieModelList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("popularMovies")){
            movieModelList = new ArrayList<>();
        } else {
            movieModelList = savedInstanceState.getParcelableArrayList("popularMovies");
        }
    }

    /**
     * this will fetch the popular movies in the background thread.
     */
    private void updatePopularMovies() {
        FetchPopularMoviesTask popularMoviesTask = new FetchPopularMoviesTask();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortByValue = sharedPreferences.getString(getString(R.string.pref_sortBy_key),
                getString(R.string.pref_sortBy_popular_value));
        Log.v(LOG_TAG,"Fetching Popular Movies....");
        popularMoviesTask.execute(sortByValue);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        int actualPosterViewWidth = Util.getImageWidth(getContext());

        movieAdapter = new MovieAdapter(getActivity(), actualPosterViewWidth, movieModelList);


        GridView gridview = (GridView) rootView.findViewById(R.id.movie_grid);
        gridview.setAdapter(movieAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                MovieModel movieModel = movieAdapter.getItem(position);
                Intent movieDetailsIntent = new Intent(getActivity(), MovieDetailsActivity.class);
                movieDetailsIntent.putExtra("movieDetails", movieModel);
                startActivity(movieDetailsIntent);
            }
        });
        return rootView;

    }

    /**
     * queries themoviedb.org API in background thread
     * parse the API response and set the adapter
     */
    public class FetchPopularMoviesTask extends AsyncTask<String, Void, List<MovieModel>> {
        private final String LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();

        @Override
        protected List<MovieModel> doInBackground(String... sortByParams) {

            final String SORT_ORDER = ".desc";
            String sortBy = sortByParams[0]+SORT_ORDER;
            String apiKey = BuildConfig.MOVIE_DB_ORG_API_KEY;

            List<MovieModel> results = null;
            try {
               results  = TMDB_Service.getPopularMovies(sortBy,apiKey);
                Log.v(LOG_TAG,"Results Length = "+results.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        /**
         * sets the movieAdapter
         * @param movieModels
         */
        @Override
        protected void onPostExecute(List<MovieModel> movieModels) {
            if (movieModels != null) {
                Log.v(LOG_TAG,"Adding movie metadata to adapter");
                movieAdapter.clear();
                movieAdapter.addAll(movieModels);
            }
        }
    }
}