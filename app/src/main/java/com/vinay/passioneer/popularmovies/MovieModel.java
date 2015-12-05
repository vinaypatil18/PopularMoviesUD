package com.vinay.passioneer.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * implementing Parcelable so that we can send the movie metadata
 * quickly to other activity without having to re-query for it
 */
public class MovieModel implements Parcelable{

    private String title;
    private String overview;
    private String posterPath;
    private int popularity;
    private int id;
    private int voteCount;
    private double voteAverage;
    private String releaseDate;

    public MovieModel() {

    }

    protected MovieModel(Parcel in) {
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        popularity = in.readInt();
        id = in.readInt();
        voteCount = in.readInt();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeInt(popularity);
        dest.writeInt(id);
        dest.writeInt(voteCount);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
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
