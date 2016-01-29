package com.vinay.passioneer.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.vinay.passioneer.popularmovies.data.TMDB_Contract;
import com.vinay.passioneer.popularmovies.model.MovieModel;
import com.vinay.passioneer.popularmovies.services.TMDB_Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PopularMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieAdapter movieAdapter;
    private FavouriteMoviesAdapter mFavouriteMoviesAdapter;
    private ArrayList<MovieModel> movieModelList;
    private static final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();
    private String sortByValue;
    private boolean isFavourite = false;
    private static final int CURSOR_LOADER_ID = 0;

    public PopularMoviesFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isFavourite) {
            updatePopularMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "Saving Instance");
        outState.putParcelableArrayList("popularMovies", movieModelList);
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
    private void updatePopularMovies() {
        FetchPopularMoviesTask popularMoviesTask = new FetchPopularMoviesTask();
        Log.v(LOG_TAG, "Fetching Popular Movies....");
        popularMoviesTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.isDataChanged) {
            Util.isDataChanged = false;
            getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
            if (mFavouriteMoviesAdapter != null)
                mFavouriteMoviesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (isFavourite) {
            Cursor cursor = getActivity()
                    .getContentResolver()
                    .query(TMDB_Contract.FavouriteMoviesEntry.CONTENT_URI,
                            null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
                cursor.close();
            } else {
                Toast.makeText(getContext(), "You have not selected any Favourite Movie", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sortByValue = sharedPreferences.getString(getString(R.string.pref_sortBy_key),
                getString(R.string.pref_sortBy_popular_value));

        String favourite = getString(R.string.pref_sortBy_favourite_value);
        if (favourite.equals(sortByValue))
            isFavourite = true;

        GridView gridview = (GridView) rootView.findViewById(R.id.movie_grid);
        int actualPosterViewWidth = Util.getImageWidth(getContext());
        if (!isFavourite) {
            movieAdapter = new MovieAdapter(getActivity(), actualPosterViewWidth, movieModelList);
            gridview.setAdapter(movieAdapter);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    MovieModel movieModel = movieAdapter.getItem(position);
                    Intent movieDetailsIntent = new Intent(getActivity(), MovieDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("movieID", movieModel.getId());
                    bundle.putParcelable("movieDetails", movieModel);
                    movieDetailsIntent.putExtras(bundle);
                    startActivity(movieDetailsIntent);
                }
            });
        } else {
            mFavouriteMoviesAdapter = new FavouriteMoviesAdapter(getContext(), null, 0,actualPosterViewWidth);
            gridview.setAdapter(mFavouriteMoviesAdapter);

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View v,
                                        int position, long id) {
                    //MovieModel movieModel = movieAdapter.getItem(position);
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        int movieIdIndex = cursor.getColumnIndex(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_ID);
                        int movieID = cursor.getInt(movieIdIndex);
                        Intent movieDetailsIntent = new Intent(getActivity(), MovieDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isDataFromDB", true);
                        bundle.putInt("movieID", movieID);
                        movieDetailsIntent.putExtras(bundle);
                        startActivity(movieDetailsIntent);
                    }

                }
            });
        }

        return rootView;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                TMDB_Contract.FavouriteMoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mFavouriteMoviesAdapter != null)
            mFavouriteMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mFavouriteMoviesAdapter != null)
            mFavouriteMoviesAdapter.swapCursor(null);
    }

    /**
     * queries themoviedb.org API in background thread
     * parse the API response and set the adapter
     */
    public class FetchPopularMoviesTask extends AsyncTask<Void, Void, List<MovieModel>> {
        private final String LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();

        @Override
        protected List<MovieModel> doInBackground(Void... sortByParams) {

            List<MovieModel> results = null;
            final String SORT_ORDER = ".desc";
            String sortBy = sortByValue + SORT_ORDER;
            try {
                results = TMDB_Service.getPopularMovies(sortBy, Util.API_KEY);
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
                movieAdapter.addAll(movieModels);
            }
        }
    }
}