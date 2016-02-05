package com.vinay.passioneer.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vinay.passioneer.popularmovies.model.MovieModel;

import java.util.List;

/**
 * Defining Custom Array Adapter
 */
public class MovieAdapter extends ArrayAdapter<MovieModel> {

    private int posterWidth;
    private int posterHeight;

    public MovieAdapter(Context context, int actualPosterViewWidth, List<MovieModel> objects) {
        super(context, 0, objects);
        this.posterWidth = actualPosterViewWidth;
        //keeping height greater than the width
        // so dividing by 0.66
        this.posterHeight = (int)(posterWidth/0.66);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieModel movieModel = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_grid_item,parent,false);
        }

        ImageView imageView = (ImageView)convertView.findViewById(R.id.movie_poster_image);
        Picasso.with(getContext())
                .load(movieModel.getPosterPath())
                .resize(posterWidth,posterHeight)
                .into(imageView);
        if(position == 0) {
            //TODO: check if mTwoPane is true
            GridView  gridView = (GridView) parent;
            gridView.performItemClick(convertView,position,getItemId(position));
        }

        return convertView;
    }
}
