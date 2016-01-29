package com.vinay.passioneer.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vinay.passioneer.popularmovies.data.TMDB_Contract;
import com.vinay.passioneer.popularmovies.model.MovieModel;
import com.vinay.passioneer.popularmovies.model.Reviews;
import com.vinay.passioneer.popularmovies.model.Trailers;
import com.vinay.passioneer.popularmovies.services.TMDB_Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class MovieDetailsFragment extends Fragment {

    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    private String shareFirstTrailerURL = null;
    private ShareActionProvider mShareActionProvider;

    // this will ensure that we have to either call the api or fetch data from DB
    private boolean isDataFromDB = false;

    private int movieID;
    private String movieName;
    private boolean addOrRemoveFromDB = false;
    private boolean favouriteButtonClicked = false;
    private List<Reviews> reviews = new ArrayList<>();
    private List<Trailers> trailers = new ArrayList<>();
    private FloatingActionButton favouriteButton;
    private MovieModel movieModel = new MovieModel();

    private LinearLayout reviewsLayout;
    private LinearLayout trailersLayout;

    private TextView userReviewsText;
    private TextView movieTrailerText;
    private String trailerKey;
    private List<String> trailerKeys;

    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ImageView backdrop_image = (ImageView) rootView.findViewById(R.id.backdrop_image);
        ImageView poster = (ImageView) rootView.findViewById(R.id.movie_poster_image);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
        TextView userRating = (TextView) rootView.findViewById(R.id.user_rating);
        TextView overview = (TextView) rootView.findViewById(R.id.movie_overview);
        reviewsLayout = (LinearLayout) rootView.findViewById(R.id.reviews);
        trailersLayout = (LinearLayout) rootView.findViewById(R.id.trailers);
        userReviewsText = (TextView) rootView.findViewById(R.id.user_reviews);
        movieTrailerText = (TextView) rootView.findViewById(R.id.movie_trailers);
        Bundle bundle = getActivity().getIntent().getExtras();
        isDataFromDB = bundle.getBoolean("isDataFromDB", false);
        int imageWidth = Util.getImageWidth(getContext());
        int imageHeight = (int) (imageWidth / 0.66);

        movieID = bundle.getInt("movieID", 0);
        Cursor cursor = null;
        if (movieID != 0) {
            Uri uri = TMDB_Contract.FavouriteMoviesEntry.buildFavouriteMoviesUri(movieID);
            cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst() && !isDataFromDB) {
                    isDataFromDB = true;
                }
            }
        }
        if (!isDataFromDB) {
            movieModel = bundle.getParcelable("movieDetails");

            Log.v(LOG_TAG, "Setting required views for movie details...");

            Picasso.with(getContext())
                    .load(movieModel.getBackdrop_path())
                    .into(backdrop_image);

            Picasso.with(getContext())
                    .load(movieModel.getPosterPath())
                    .resize(imageWidth, imageHeight)
                    .into(poster);

            releaseDate.setText(movieModel.getReleaseDate());

            userRating.setText(movieModel.getVoteAverage() + "/10");

            overview.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
            Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
            overview.setTypeface(typeface);
            overview.setText(movieModel.getOverview());

            movieID = movieModel.getId();
            movieName = movieModel.getOriginalTitle();
        } else {
            if (cursor != null) {
                int releaseDateIndex = cursor.getColumnIndex(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE);
                String movieReleaseDate = cursor.getString(releaseDateIndex);
                releaseDate.setText(movieReleaseDate);

                int voteAvgIndex = cursor.getColumnIndex(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_AVG);
                Double voteAvg = cursor.getDouble(voteAvgIndex);
                userRating.setText(voteAvg + "/10");

                int movieNameIndex = cursor.getColumnIndex(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_NAME);
                movieName = cursor.getString(movieNameIndex);

                int overviewIndex = cursor.getColumnIndex(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_SYNOPSIS);
                String synopsis = cursor.getString(overviewIndex);
                overview.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
                Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
                overview.setTypeface(typeface);
                overview.setText(synopsis);

                String path = getContext().getFilesDir().getAbsolutePath();
                Picasso.with(getContext())
                        .load("file:" + path + "/" + movieID + ".jpg")
                        .resize(imageWidth, imageHeight)
                        .into(poster);

                Picasso.with(getContext())
                        .load("file:" + path + "/" + movieID + "_backdrop.jpg")
                        .into(backdrop_image);

                cursor.close();
                setMovieReviewsFromDB(movieID);
                setMovieTrailersFromDB(movieID);
            }
        }
        return rootView;
    }


    private void setMovieTrailersFromDB(int movieID) {
        Uri uri = TMDB_Contract.TrailersEntry.buildTrailersUri(movieID);
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            trailerKeys = new ArrayList<>();
            movieTrailerText.setText(getString(R.string.trailers) + " (" + cursor.getCount() + ")");
            while (cursor.moveToNext()) {
                int trailerKeyIndex = cursor.getColumnIndex(TMDB_Contract.TrailersEntry.COLUMN_TRAILER_KEY);
                final String trailerDBKey = cursor.getString(trailerKeyIndex);
                ImageView imageView = new ImageView(getContext());
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(0, 0, 8, 0);
                imageView.setAdjustViewBounds(true);
                String path = getContext().getFilesDir().getAbsolutePath();
                Picasso.with(getContext())
                        .load("file:" + path + "/" + movieID + "_" + trailerDBKey + ".jpg")
                        .resize(300, 450)
                        .into(imageView);

                trailersLayout.addView(imageView);
                trailerKeys.add(trailerDBKey);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String videoId = trailerDBKey;
                        Uri trailerURI = Uri.parse("http://www.youtube.com/watch?v=" + videoId);
                        Intent intent = new Intent(Intent.ACTION_VIEW, trailerURI);
                        startActivity(intent);
                    }
                });
            }
            if (cursor.moveToFirst()) {
                int trailerKeyIndex = cursor.getColumnIndex(TMDB_Contract.TrailersEntry.COLUMN_TRAILER_KEY);
                String trailerDBKey = cursor.getString(trailerKeyIndex);
                shareFirstTrailerURL = "Check-out " + movieName + " trailer \n" + Util.BASE_YOUTUBE_URL + trailerDBKey;
            }
            cursor.close();
        }
    }

    private void setMovieReviewsFromDB(int movieID) {
        Uri uri = TMDB_Contract.ReviewsEntry.buildReviewsUri(movieID);
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            userReviewsText.setText(getString(R.string.userReviews) + " (" + cursor.getCount() + ")");
            while (cursor.moveToNext()) {
                int authorIndex = cursor.getColumnIndex(TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_AUTHOR);
                String author = cursor.getString(authorIndex);

                TextView textView = new TextView(getContext());
                textView.setText(author);
                textView.setPadding(16, 16, 16, 16);
                textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
                Typeface typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
                textView.setTypeface(typeface);
                reviewsLayout.addView(textView);

                int contentIndex = cursor.getColumnIndex(TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_CONTENT);
                String content = cursor.getString(contentIndex);
                textView = new TextView(getContext());
                textView.setText(content);
                textView.setPadding(16, 16, 16, 16);
                textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Holo_Medium);
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
                textView.setTypeface(typeface);
                reviewsLayout.addView(textView);

                View view = new View(getContext());
                view.setMinimumHeight(3);

                view.setBackgroundColor(Color.DKGRAY);
                view.setPadding(8, 8, 8, 8);
                reviewsLayout.addView(view);
            }
            cursor.close();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        favouriteButton = (FloatingActionButton) getActivity().findViewById(R.id.image_favourite);
        if (!isDataFromDB) {
            ReviewsAndTrailersTask reviewsAndTrailersTask = new ReviewsAndTrailersTask();
            reviewsAndTrailersTask.execute();
        }
        if (isDataFromDB) {
            addOrRemoveFromDB = true;
            favouriteButton.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
        }
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorStateList colorStateList = favouriteButton.getBackgroundTintList();
                int defaultColor = colorStateList !=null ? colorStateList.getDefaultColor():0;
                boolean flag = false;
                if (defaultColor == Color.BLUE) {
                    flag = true;
                    //favouriteButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                }
                favouriteButtonClicked = true;
                if (!addOrRemoveFromDB && !isDataFromDB) {
                    addOrRemoveFromDB = true;
                    favouriteButton.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_star_black_18dp));
                    favouriteButton.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
                    Toast.makeText(getContext(), "Added to Favourite", Toast.LENGTH_SHORT).show();
                } else {
                    addOrRemoveFromDB = false;
                    if (isDataFromDB) {
                        // if data is from DB i.e movie is already favourite
                        // then only it make sense to remove it.
                        if (!flag) {
                            favouriteButton.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_star_off_white_18dp));
                            favouriteButton.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                            Toast.makeText(getContext(), "Removed from Favourite", Toast.LENGTH_SHORT).show();
                        } else {
                            addOrRemoveFromDB = true;
                            favouriteButton.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_star_black_18dp));
                            favouriteButton.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
                        }
                    } else {
                        // set to original color, user does not want to add to favourite for now
                        favouriteButton.setBackgroundTintList(ColorStateList.valueOf(Color.MAGENTA));
                    }
                }
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_details, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        mShareActionProvider.setShareIntent(createShareTrailerIntent());
        super.onCreateOptionsMenu(menu, inflater);
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareFirstTrailerURL);
        return shareIntent;
    }

    private void setMovieTrailers(List<Trailers> trailersList) {


        if (trailersList != null && trailersList.size() > 0) {
            shareFirstTrailerURL = "Check-out " + movieName + " trailer \n" + Util.BASE_YOUTUBE_URL + trailersList.get(0).getKey();
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }

        TextView movieTrailersText = (TextView) getActivity().findViewById(R.id.movie_trailers);
        movieTrailersText.setText(getString(R.string.trailers) + " (" + trailersList.size() + ")");
        for (final Trailers trailer : trailersList) {
            ImageView imageView = new ImageView(getContext());
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(0, 0, 8, 0);
            String video_thumbnail = "http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";
            imageView.setAdjustViewBounds(true);
            Picasso.with(getContext())
                    .load(video_thumbnail)
                    .resize(300, 450)
                    .into(imageView);
            trailersLayout.addView(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String videoId = trailer.getKey();
                    Uri trailerURI = Uri.parse(Util.BASE_YOUTUBE_URL + videoId);
                    Intent intent = new Intent(Intent.ACTION_VIEW, trailerURI);
                    startActivity(intent);
                }
            });
        }
    }


    private void setMovieReviews(List<Reviews> movieReviews) {

        LinearLayout reviewsLayout = (LinearLayout) getActivity().findViewById(R.id.reviews);
        TextView userReviewsText = (TextView) getActivity().findViewById(R.id.user_reviews);
        userReviewsText.setText(getString(R.string.userReviews) + " (" + movieReviews.size() + ")");
        for (Reviews review : movieReviews) {
            TextView textView = new TextView(getContext());
            textView.setText(review.getAuthor());
            textView.setPadding(16, 16, 16, 16);
            textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
            Typeface typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            textView.setTypeface(typeface);
            reviewsLayout.addView(textView);

            textView = new TextView(getContext());
            textView.setText(review.getContent());
            textView.setPadding(16, 16, 16, 16);
            textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Holo_Medium);
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
            textView.setTypeface(typeface);
            reviewsLayout.addView(textView);

            View view = new View(getContext());
            view.setMinimumHeight(3);
            view.setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.darker_gray));
            view.setPadding(8, 8, 8, 8);
            reviewsLayout.addView(view);
        }
    }

    Target posterTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d("Target: ", "Inside onBitmapLoaded");
            FileOutputStream outputStream;
            try {
                String imageName;
                imageName = String.valueOf(movieID) + ".jpg";
                Log.d("Target: ", imageName);
                outputStream = getActivity().openFileOutput(imageName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                outputStream.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("Target: ", "onBitmapFailed: ");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("Target: ", "onPrepareLoad: ");
        }
    };

    Target backdropTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            FileOutputStream outputStream;
            try {
                String imageName;
                imageName = String.valueOf(movieID) + "_backdrop.jpg";
                Log.d("Target: ", imageName);
                outputStream = getActivity().openFileOutput(imageName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                outputStream.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("Target: ", "onBitmapFailed: ");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("Target: ", "onPrepareLoad: ");
        }
    };

    Target trailerTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            FileOutputStream outputStream;
            try {
                String imageName;
                imageName = String.valueOf(movieID) + "_" + trailerKey + ".jpg";
                Log.d("Target: ", imageName);
                outputStream = getActivity().openFileOutput(imageName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                outputStream.close();
            }catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("Target: ", "onBitmapFailed: ");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("Target: ", "onPrepareLoad: ");
        }
    };


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (favouriteButtonClicked) {
            // then only perform the DB operation otherwise every time it will go to else part
            String filePath = getContext().getFilesDir().getAbsolutePath();
            if (addOrRemoveFromDB) {
                if (!isDataFromDB) {
                    //add to the favourite DB

                    ContentValues movieContentValues = new ContentValues();
                    movieContentValues.put(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_ID, movieID);
                    movieContentValues.put(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_NAME, movieModel.getOriginalTitle());
                    movieContentValues.put(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE, movieModel.getReleaseDate());
                    movieContentValues.put(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_COUNT, movieModel.getVoteCount());
                    movieContentValues.put(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_AVG, movieModel.getVoteAverage());
                    movieContentValues.put(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_SYNOPSIS, movieModel.getOverview());
                    movieContentValues.put(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_BACKDROP_IMAGE_PATH, filePath);
                    movieContentValues.put(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH, filePath);


                    Picasso.with(getContext())
                            .load(movieModel.getPosterPath())
                            .into(posterTarget);
                    Picasso.with(getContext())
                            .load(movieModel.getBackdrop_path())
                            .into(backdropTarget);


                    getContext().getContentResolver().insert(TMDB_Contract.FavouriteMoviesEntry.CONTENT_URI, movieContentValues);
                    Vector<ContentValues> reviewContentValuesVector = new Vector<>(reviews.size());
                    for (Reviews review : reviews) {
                        ContentValues reviewContentValues = new ContentValues();
                        reviewContentValues.put(TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_ID, review.getId());
                        reviewContentValues.put(TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, review.getAuthor());
                        reviewContentValues.put(TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_CONTENT, review.getContent());
                        reviewContentValues.put(TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_MOVIE_ID, movieID);

                        reviewContentValuesVector.add(reviewContentValues);
                    }
                    if (reviewContentValuesVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[reviewContentValuesVector.size()];
                        reviewContentValuesVector.toArray(cvArray);
                        int inserted = getContext().getContentResolver().bulkInsert(TMDB_Contract.ReviewsEntry.CONTENT_URI, cvArray);
                    }

                    Vector<ContentValues> trailerContentValuesVector = new Vector<>(trailers.size());
                    for (Trailers trailer : trailers) {
                        ContentValues trailerContentValues = new ContentValues();
                        trailerContentValues.put(TMDB_Contract.TrailersEntry.COLUMN_TRAILER_ID, trailer.getId());
                        trailerContentValues.put(TMDB_Contract.TrailersEntry.COLUMN_TRAILER_KEY, trailer.getKey());
                        trailerContentValues.put(TMDB_Contract.TrailersEntry.COLUMN_TRAILER_NAME, trailer.getName());
                        trailerContentValues.put(TMDB_Contract.TrailersEntry.COLUMN_TRAILER_MOVIE_ID, movieID);

                        trailerContentValuesVector.add(trailerContentValues);
                        trailerKey = trailer.getKey();
                        String video_thumbnail = "http://img.youtube.com/vi/" + trailerKey + "/0.jpg";
                        Picasso.with(getContext())
                                .load(video_thumbnail)
                                .resize(300, 450)
                                .into(trailerTarget);
                    }
                    if (trailerContentValuesVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[trailerContentValuesVector.size()];
                        trailerContentValuesVector.toArray(cvArray);
                        int inserted = getContext().getContentResolver().bulkInsert(TMDB_Contract.TrailersEntry.CONTENT_URI, cvArray);
                    }
                    // since the movie is added to DB now,make this flag true
                    // so even if onPause gets called multiple times, there won't
                    // be a DB call made.
                    isDataFromDB = true;
                }
            } else {
                if (isDataFromDB) {
                    // remove from favourite DB
                    getContext().getContentResolver()
                            .delete(TMDB_Contract.ReviewsEntry.CONTENT_URI,
                                    TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_MOVIE_ID + "=?",
                                    new String[]{String.valueOf(movieID)});

                    getContext().getContentResolver()
                            .delete(TMDB_Contract.TrailersEntry.CONTENT_URI,
                                    TMDB_Contract.TrailersEntry.COLUMN_TRAILER_MOVIE_ID + "=?",
                                    new String[]{String.valueOf(movieID)});

                    getContext().getContentResolver()
                            .delete(TMDB_Contract.FavouriteMoviesEntry.CONTENT_URI,
                                    TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_ID + "=?",
                                    new String[]{String.valueOf(movieID)});

                    //remove the image from the internal storage
                    getContext().deleteFile(movieID + ".jpg");
                    getContext().deleteFile(movieID + "_backdrop.jpg");
                    for (String trailerKey : trailerKeys) {
                        getContext().deleteFile(movieID + "_" + trailerKey + ".jpg");
                    }
                    Util.isDataChanged = true;
                }
            }
        }
    }

    public class ReviewsAndTrailersTask extends AsyncTask<Void, Void, Map<String, Object>> {

        Map<String, Object> reviewsAndTrailersMap = new HashMap<>(2);

        @Override
        protected Map<String, Object> doInBackground(Void... params) {
            try {
                List<Reviews> movieReviews = TMDB_Service.getMovieReviews(movieID, Util.API_KEY);

                List<Trailers> movieTrailers = TMDB_Service.getMovieTrailers(movieID, Util.API_KEY);
                reviewsAndTrailersMap.put("reviews", movieReviews);
                reviewsAndTrailersMap.put("trailers", movieTrailers);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return reviewsAndTrailersMap;
        }

        @Override
        protected void onPostExecute(Map<String, Object> reviewsAndTrailersMap) {
            if (reviewsAndTrailersMap != null) {
                reviews = (List<Reviews>) reviewsAndTrailersMap.get("reviews");
                trailers = (List<Trailers>) reviewsAndTrailersMap.get("trailers");

                setMovieReviews(reviews);
                setMovieTrailers(trailers);

            }
        }
    }
}
