package com.vinay.passioneer.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vinay.passioneer.popularmovies.model.MovieModel;

import java.util.List;

/**
 * Defining Custom Array Adapter
 */
public class MovieAdapter extends ArrayAdapter<MovieModel> {

    private int posterWidth;
    private int posterHeight;
    public static int cnt;

    public MovieAdapter(Context context, int actualPosterViewWidth, List<MovieModel> objects) {
        super(context, 0, objects);
        cnt = 0;
        this.posterWidth = actualPosterViewWidth;
        //keeping height greater than the width
        // so dividing by 0.66
        this.posterHeight = (int) (posterWidth / 0.66);
    }

    public static class MovieViewHolder {

        public final ImageView posterImageView;
        public final TextView popularityTextView;
        public final TextView votesTextView;

        public MovieViewHolder(View view) {
            posterImageView = (ImageView) view.findViewById(R.id.movie_poster_image);
            popularityTextView = (TextView) view.findViewById(R.id.popularity_percent);
            votesTextView = (TextView) view.findViewById(R.id.vote_count);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieModel movieModel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_grid_item, parent, false);
            MovieViewHolder movieViewHolder = new MovieViewHolder(convertView);
            convertView.setTag(movieViewHolder);
        }

        MovieViewHolder movieViewHolder = (MovieViewHolder) convertView.getTag();

        long percent = Math.round(movieModel.getVoteAverage() * 10);
        movieViewHolder.popularityTextView.setText(String.valueOf(percent) + "%");

        movieViewHolder.votesTextView.setText(String.valueOf(movieModel.getVoteCount())+" votes");

        Picasso.with(getContext())
                .load(movieModel.getPosterPath())
                .resize(posterWidth, posterHeight)
                .into(movieViewHolder.posterImageView);

        if(position == 0 && cnt == 0 && Util.isMultiPane) {
            cnt++; // when we scroll, sometimes the first item gets selected.
            GridView gridView = (GridView) parent;
            gridView.performItemClick(convertView,position,getItemId(position));
        }

        return convertView;
    }
}
