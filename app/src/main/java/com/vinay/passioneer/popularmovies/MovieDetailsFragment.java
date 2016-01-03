package com.vinay.passioneer.popularmovies;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vinay.passioneer.popularmovies.model.MovieModel;
import com.vinay.passioneer.popularmovies.model.Reviews;
import com.vinay.passioneer.popularmovies.model.Trailers;
import com.vinay.passioneer.popularmovies.services.TMDB_Service;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

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


        MovieModel movieModel = getActivity().getIntent().getParcelableExtra("movieDetails");

        final int imageWidth = Util.getImageWidth(getContext());
        final int imageHeight = (int) (imageWidth / 0.66);

        Log.v(LOG_TAG, "Setting required views for movie details...");
        ImageView backdrop_image = (ImageView) rootView.findViewById(R.id.backdrop_image);
        Picasso.with(getContext())
                .load(movieModel.getBackdrop_path())
                .into(backdrop_image);

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
        overview.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
        Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        overview.setTypeface(typeface);
        overview.setText(movieModel.getOverview());


        int movieID = movieModel.getId();

        final LinearLayout reviews = (LinearLayout) rootView.findViewById(R.id.reviews);
        setMovieReviews(movieID, reviews);

        final LinearLayout trailers = (LinearLayout) rootView.findViewById(R.id.trailers);
        setMovieTrailers(movieID, trailers,imageHeight,imageWidth);

        return rootView;
    }

    /**
     * get the movie trailers by calling TMDB API asynchronously
     * @param movieID
     * @param trailers
     * @param imageHeight
     * @param imageWidth
     */
    private void setMovieTrailers(int movieID, final LinearLayout trailers, final int imageHeight, final int imageWidth) {
        Call<Trailers.TrailersResponse> trailersResponseCall = TMDB_Service.getMovieTrailers(movieID, Util.API_KEY);
        trailersResponseCall.enqueue(new Callback<Trailers.TrailersResponse>() {
            @Override
            public void onResponse(Response<Trailers.TrailersResponse> response, Retrofit retrofit) {
                if (response != null && response.isSuccess()) {
                    Trailers.TrailersResponse trailersResponse = response.body();
                    List<Trailers> trailersList = trailersResponse.getTrailers();

                    for (final Trailers trailer : trailersList) {
                        String video_thumbnail = "http://img.youtube.com/vi/";
                        ImageView imageView = new ImageView(getContext());
                        imageView.setAdjustViewBounds(true);
                        imageView.setPadding(0, 0, 8, 0);
                        video_thumbnail = video_thumbnail + trailer.getKey() + "/0.jpg";
                        imageView.setAdjustViewBounds(true);
                        Picasso.with(getContext())
                                .load(video_thumbnail)
                                .resize(imageWidth, imageHeight)
                                .into(imageView);
                        trailers.addView(imageView);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String videoId = trailer.getKey();
                                Uri trailerURI = Uri.parse("http://www.youtube.com/watch?v=" + videoId);
                                Intent intent = new Intent(Intent.ACTION_VIEW, trailerURI);
                                startActivity(intent);
                            }
                        });

                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
            }
        });
    }


    /**
     * get the movie reviews by calling TMDB API asynchronously
     * @param movieID
     * @param reviews
     */
    private void setMovieReviews(int movieID, final LinearLayout reviews) {

        Call<Reviews.ReviewsResponse> reviewsResponseCall = TMDB_Service.getMovieReviews(movieID, Util.API_KEY);
        reviewsResponseCall.enqueue(new Callback<Reviews.ReviewsResponse>() {
            @Override
            public void onResponse(Response<Reviews.ReviewsResponse> response, Retrofit retrofit) {
                if (response != null && response.isSuccess()) {
                    Reviews.ReviewsResponse reviewsResponse = response.body();
                    List<Reviews> reviewsList = reviewsResponse.getReviews();
                    for (Reviews review : reviewsList) {
                        TextView textView = new TextView(getContext());
                        textView.setText(review.getAuthor());
                        textView.setPadding(16, 16, 16, 16);
                        textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
                        Typeface typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
                        textView.setTypeface(typeface);
                        reviews.addView(textView);

                        textView = new TextView(getContext());
                        textView.setText(review.getContent());
                        textView.setPadding(16, 16, 16, 16);
                        textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Holo_Medium);
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
                        textView.setTypeface(typeface);
                        reviews.addView(textView);

                        View view = new View(getContext());
                        view.setMinimumHeight(3);
                        view.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        view.setPadding(8, 8, 8, 8);
                        reviews.addView(view);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
            }
        });
    }
}
