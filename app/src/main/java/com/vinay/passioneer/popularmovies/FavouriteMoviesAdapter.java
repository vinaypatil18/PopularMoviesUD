package com.vinay.passioneer.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vinay.passioneer.popularmovies.data.TMDB_Contract;

public class FavouriteMoviesAdapter extends CursorAdapter {
    private static final String LOG_TAG = FavouriteMoviesAdapter.class.getSimpleName();
    private int posterWidth;
    private int posterHeight;

    public static class ViewHolder {

        public final ImageView posterImageView;
        public final TextView popularityTextView;
        public final TextView votesTextView;

        public ViewHolder(View view) {
            posterImageView = (ImageView) view.findViewById(R.id.movie_poster_image);
            popularityTextView = (TextView) view.findViewById(R.id.popularity_percent);
            votesTextView = (TextView) view.findViewById(R.id.vote_count);
        }
    }

    public FavouriteMoviesAdapter(Context context, Cursor c, int flags, int actualPosterViewWidth) {
        super(context, c, flags);
        this.posterWidth = actualPosterViewWidth;
        this.posterHeight = (int) (posterWidth / 0.66);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.movie_grid_item;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int movieIdIndex = cursor.getColumnIndex(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_ID);
        int movieID = cursor.getInt(movieIdIndex);

        int voteCountIndex = cursor.getColumnIndex(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_COUNT);
        int voteCount = cursor.getInt(voteCountIndex);
        viewHolder.votesTextView.setText(String.valueOf(voteCount) + " votes");

        int voteAvgIndex = cursor.getColumnIndex(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_AVG);
        Double voteAvg = cursor.getDouble(voteAvgIndex);
        long percent = Math.round(voteAvg * 10);
        viewHolder.popularityTextView.setText(String.valueOf(percent) + "%");

        String path = mContext.getFilesDir().getAbsolutePath();
        Picasso.with(mContext)
                .load("file:" + path + "/" + movieID + ".jpg")
                .resize(posterWidth, posterHeight)
                .into(viewHolder.posterImageView);
    }
}
