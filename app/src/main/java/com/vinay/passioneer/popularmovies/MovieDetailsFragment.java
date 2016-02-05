package com.vinay.passioneer.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vinay.passioneer.popularmovies.model.MovieModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    private final String LOG_TAG = "MovieDetailsFragment";

    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        Bundle bundle = getArguments();
        MovieModel movieModel = new MovieModel();
        if (bundle != null) {
            movieModel = bundle.getParcelable("movieDetails");

            Log.v(LOG_TAG, "Setting required views for movie details...");
            ImageView backdrop_image = (ImageView) rootView.findViewById(R.id.backdrop_image);
            Picasso.with(getContext())
                    .load(movieModel.getBackdrop_path())
                    .into(backdrop_image);


            int imageWidth = Util.getImageWidth(getContext());
            int imageHeight = (int) (imageWidth / 0.66);
            ImageView poster = (ImageView) rootView.findViewById(R.id.movie_poster_image);
            Picasso.with(getContext())
                    .load(movieModel.getPosterPath())
                    .resize(imageWidth, imageHeight)
                    .into(poster);

            TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
            releaseDate.setText(movieModel.getReleaseDate());

            TextView userRating = (TextView) rootView.findViewById(R.id.user_rating);
            userRating.setText(movieModel.getVoteAverage() + "/10");

            TextView overview = (TextView) rootView.findViewById(R.id.movie_overview);
            overview.setText(movieModel.getOverview());
        }
        return rootView;
    }
}
