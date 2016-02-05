package com.vinay.passioneer.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private static final String LOG_TAG = "PopularMoviesFragment";
    private Bundle mSavedInstanceState;
    private boolean flag = false;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    // this will be used in the MainActivity for two-pane mode
    // so that the detail fragment will contain the data


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface TMDB_Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(MovieModel movieModel);
    }

    public PopularMoviesFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "Saving Instance");
        outState.putParcelableArrayList("popularMovies", movieModelList);

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("popularMovies")) {
            movieModelList = new ArrayList<>();
        } else {
            movieModelList = savedInstanceState.getParcelableArrayList("popularMovies");
        }
    }

    /**
     * this will fetch the popular movies in the background thread.
     */
    public void updatePopularMovies() {
        FetchPopularMoviesTask popularMoviesTask = new FetchPopularMoviesTask();
        String sortByValue = Util.getPreferredSortByOption(getActivity());
        Log.v(LOG_TAG, "Fetching Popular Movies....");
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

        gridview.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                MovieModel movieModel = movieAdapter.getItem(position);
                ((TMDB_Callback) getActivity()).onItemSelected(movieModel);

                mPosition = position;
            }

        });

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
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
            String sortBy = sortByParams[0] + SORT_ORDER;
            String apiKey = BuildConfig.MOVIE_DB_ORG_API_KEY;

            List<MovieModel> results = null;
            try {
                results = TMDB_Service.getPopularMovies(sortBy, apiKey);
                Log.v(LOG_TAG, "Results Length = " + results.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        /**
         * sets the movieAdapter
         *
         * @param movieModels
         */
        @Override
        protected void onPostExecute(List<MovieModel> movieModels) {
            if (movieModels != null) {
                Log.v(LOG_TAG, "Adding movie metadata to adapter");
                movieAdapter.clear();
                flag = true;// this flag is for handling scroll position
                movieAdapter.addAll(movieModels);

            }
        }
    }
}