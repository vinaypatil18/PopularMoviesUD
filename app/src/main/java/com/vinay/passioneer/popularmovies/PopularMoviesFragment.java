package com.vinay.passioneer.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
            movieModelList = new ArrayList<MovieModel>();
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
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String popularMoviesJsonStr = null;

            try {

                //base URL
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                //this will be used in either case
                final String QUERY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";
                final String SORT_ORDER = ".desc";
                //sortByParams will either have popularity or vote_count value
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, sortByParams[0] + SORT_ORDER)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_ORG_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to themoviedb.org, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                popularMoviesJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movies JSON string: " + popularMoviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMoviesDataFromJSON(popularMoviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG,"Error in parsing JSON response : "+e.getMessage());
            }
            return null;
        }

        /**
         * parse the required movie metadata
         * @param popularMoviesJsonStr
         * @return movieModelList to set the adapter
         * @throws JSONException
         */
        private List<MovieModel> getMoviesDataFromJSON(String popularMoviesJsonStr) throws JSONException {

            final String ORIGINAL_TITLE = "original_title";
            final String OVERVIEW = "overview";
            final String VOTE_COUNT = "vote_count";
            final String VOTE_AVERAGE = "vote_average";
            final String POPULARITY = "popularity";
            final String ID = "id";
            final String POSTER_PATH = "poster_path";
            final String RELEASE_DATE = "release_date";
            final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
            final String RESULTS = "results";

            JSONObject popularMoviesJSON = new JSONObject(popularMoviesJsonStr);
            JSONArray resultsArray = popularMoviesJSON.getJSONArray(RESULTS);

            Log.v(LOG_TAG, "results length = " + resultsArray.length());
            List<MovieModel> movieModels = new ArrayList<>();
            MovieModel movieModel;
            for (int i = 0; i < resultsArray.length(); i++) {
                movieModel = new MovieModel();

                JSONObject movieDetailsJSON = resultsArray.getJSONObject(i);
                movieModel.setTitle(movieDetailsJSON.getString(ORIGINAL_TITLE));
                movieModel.setOverview(movieDetailsJSON.getString(OVERVIEW));
                movieModel.setPosterPath(BASE_IMAGE_URL + movieDetailsJSON.getString(POSTER_PATH));
                movieModel.setPopularity(movieDetailsJSON.getInt(POPULARITY));
                movieModel.setId(movieDetailsJSON.getInt(ID));
                movieModel.setVoteCount(movieDetailsJSON.getInt(VOTE_COUNT));
                movieModel.setReleaseDate(movieDetailsJSON.getString(RELEASE_DATE));
                movieModel.setVoteAverage(movieDetailsJSON.getDouble(VOTE_AVERAGE));

                movieModels.add(movieModel);
            }
            return movieModels;
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