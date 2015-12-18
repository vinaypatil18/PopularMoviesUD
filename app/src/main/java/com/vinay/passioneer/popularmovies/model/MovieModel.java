package com.vinay.passioneer.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.vinay.passioneer.popularmovies.Util;

/**
 * implementing Parcelable so that we can send the movie metadata
 * quickly to other activity without having to re-query for it
 */
public class MovieModel implements Parcelable {

    private String original_title;
    private String overview;
    private String poster_path;
    private Double popularity;
    private Integer id;
    private Integer vote_count;
    private Double vote_average;
    private String release_date;

    protected MovieModel(Parcel in) {
        original_title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        popularity = in.readDouble();
        id = in.readInt();
        vote_count = in.readInt();
        vote_average = in.readDouble();
        release_date = in.readString();
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return Util.BASE_IMAGE_URL +poster_path;
    }

    public Double getPopularity() {
        return popularity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVoteCount() {
        return vote_count;
    }

    public Double getVoteAverage() {
        return vote_average;
    }

    public String getReleaseDate() {
        return release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(poster_path);
        dest.writeDouble(popularity);
        dest.writeInt(id);
        dest.writeInt(vote_count);
        dest.writeDouble(vote_average);
        dest.writeString(release_date);
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };
}
