package com.vinay.passioneer.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vinay.passioneer.popularmovies.data.TMDB_Contract;

public class FavouriteMoviesAdapter extends CursorAdapter {
    private static final String LOG_TAG = FavouriteMoviesAdapter.class.getSimpleName();
    private int posterWidth;
    private int posterHeight;
    private static int cnt;
    private static int tmp;

    public static class FavouriteMovieViewHolder {

        public final ImageView posterImageView;
        public final TextView popularityTextView;
        public final TextView votesTextView;
        public final GridView gridView;

        public FavouriteMovieViewHolder(View view, ViewGroup parent) {
            posterImageView = (ImageView) view.findViewById(R.id.movie_poster_image);
            popularityTextView = (TextView) view.findViewById(R.id.popularity_percent);
            votesTextView = (TextView) view.findViewById(R.id.vote_count);
            gridView = (GridView) parent;
        }
    }

    public FavouriteMoviesAdapter(Context context, Cursor c, int flags, int actualPosterViewWidth) {
        super(context, c, flags);
        this.posterWidth = actualPosterViewWidth;
        this.posterHeight = (int) (posterWidth / 0.66);
        cnt = 0;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.movie_grid_item;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        FavouriteMovieViewHolder favouriteMovieViewHolder = new FavouriteMovieViewHolder(view, parent);
        view.setTag(favouriteMovieViewHolder);
        if (cursor.isFirst() && cnt == 0 && Util.isMultiPane) {
            cnt++; // when we scroll, sometimes the first item gets selected.
            favouriteMovieViewHolder.gridView.performItemClick(view, 0, getItemId(0));
        }
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        FavouriteMovieViewHolder favouriteMovieViewHolder = (FavouriteMovieViewHolder) view.getTag();

        int movieIdIndex = cursor.getColumnIndex(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_ID);
        int movieID = cursor.getInt(movieIdIndex);

        String path = mContext.getFilesDir().getAbsolutePath();
        Picasso.with(mContext)
                .load("file:" + path + "/" + movieID + ".jpg")
                .resize(posterWidth, posterHeight)
                .into(favouriteMovieViewHolder.posterImageView);

        int voteCountIndex = cursor.getColumnIndex(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_COUNT);
        int voteCount = cursor.getInt(voteCountIndex);
        favouriteMovieViewHolder.votesTextView.setText(String.valueOf(voteCount) + " votes");

        int voteAvgIndex = cursor.getColumnIndex(TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_AVG);
        Double voteAvg = cursor.getDouble(voteAvgIndex);
        long percent = Math.round(voteAvg * 10);
        favouriteMovieViewHolder.popularityTextView.setText(String.valueOf(percent) + "%");

        if (cursor.isFirst() && Util.isMultiPane) {
            int cursorCount = cursor.getCount();

            if (tmp != 0 && cursorCount == Util.counter - 1) {
                // this means the cursor is reloaded successfully after notifyDataSetChanged
                // now we can perform the click operation
                tmp = 0;
                favouriteMovieViewHolder.gridView.performItemClick(view, 0, getItemId(0));

            }

            if (tmp == 0) {
                // get the cursor count to refer it later.
                tmp++;
                Util.counter = cursor.getCount();
            }
        }

    }
}
