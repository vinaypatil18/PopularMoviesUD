package com.vinay.passioneer.popularmovies.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TMDB_Helper extends SQLiteOpenHelper{

    public static final String LOG_TAG = TMDB_Helper.class.getSimpleName();

    //name & version
    private static final String DATABASE_NAME = "popularMovies.db";
    private static final int DATABASE_VERSION = 1;

    public TMDB_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "Creating "+DATABASE_NAME);
        final String SQL_CREATE_FAVOURITE_MOVIES_TABLE = "CREATE TABLE " +
                TMDB_Contract.FavouriteMoviesEntry.TABLE_FAVOURITE + "(" + TMDB_Contract.FavouriteMoviesEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_NAME +" TEXT NOT NULL, " +
                TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_COUNT +" INTEGER NOT NULL, " +
                TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_VOTE_AVG +" REAL NOT NULL, " +
                TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_SYNOPSIS +" TEXT NOT NULL, " +
                TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE +" TEXT NOT NULL, " +
                TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH +" TEXT, " +
                TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_BACKDROP_IMAGE_PATH +" TEXT);";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " +
                TMDB_Contract.ReviewsEntry.TABLE_REVIEWS + "(" + TMDB_Contract.ReviewsEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_ID + " INTEGER NOT NULL, " +
                TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_AUTHOR +" TEXT NOT NULL, " +
                TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_CONTENT +" TEXT NOT NULL, " +
                TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_MOVIE_ID +" INTEGER NOT NULL, " +
                " FOREIGN KEY (" + TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_MOVIE_ID + ") REFERENCES " +
                TMDB_Contract.FavouriteMoviesEntry.TABLE_FAVOURITE + " (" + TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_ID + "));";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
                TMDB_Contract.TrailersEntry.TABLE_TRAILERS + "(" + TMDB_Contract.ReviewsEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TMDB_Contract.TrailersEntry.COLUMN_TRAILER_ID + " INTEGER NOT NULL, " +
                TMDB_Contract.TrailersEntry.COLUMN_TRAILER_KEY +" TEXT NOT NULL, " +
                TMDB_Contract.TrailersEntry.COLUMN_TRAILER_NAME +" TEXT NOT NULL, " +
                TMDB_Contract.TrailersEntry.COLUMN_TRAILER_MOVIE_ID +" INTEGER NOT NULL, " +
                " FOREIGN KEY (" + TMDB_Contract.TrailersEntry.COLUMN_TRAILER_MOVIE_ID + ") REFERENCES " +
                TMDB_Contract.FavouriteMoviesEntry.TABLE_FAVOURITE + " (" + TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_ID + "));";


        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO....
    }

}
