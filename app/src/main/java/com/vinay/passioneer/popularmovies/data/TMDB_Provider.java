package com.vinay.passioneer.popularmovies.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class TMDB_Provider extends ContentProvider {

    private TMDB_Helper mOpenHelper;
    private static final String LOG_TAG = TMDB_Provider.class.getSimpleName();
    // Codes for the UriMatcher
    private static final int FAVOURITE_MOVIES = 100;
    private static final int FAVOURITE_MOVIES_WITH_ID = 101;
    private static final int REVIEWS = 200;
    private static final int REVIEWS_WITH_ID = 201;
    private static final int TRAILERS = 300;
    private static final int TRAILERS_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TMDB_Contract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, TMDB_Contract.PATH_FAVOURITE_MOVIES, FAVOURITE_MOVIES);
        matcher.addURI(authority, TMDB_Contract.PATH_FAVOURITE_MOVIES + "/#", FAVOURITE_MOVIES_WITH_ID);
        matcher.addURI(authority, TMDB_Contract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, TMDB_Contract.PATH_REVIEWS + "/#", REVIEWS_WITH_ID);
        matcher.addURI(authority, TMDB_Contract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, TMDB_Contract.PATH_TRAILERS + "/#", TRAILERS_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate Called....");
        mOpenHelper = new TMDB_Helper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        Log.v(LOG_TAG, "querying: " + uri);
        switch (sUriMatcher.match(uri)) {
            // All Favourite Movies selected
            case FAVOURITE_MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TMDB_Contract.FavouriteMoviesEntry.TABLE_FAVOURITE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual favourite movie based on Id selected
            case FAVOURITE_MOVIES_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TMDB_Contract.FavouriteMoviesEntry.TABLE_FAVOURITE,
                        projection,
                        TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }

            case REVIEWS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TMDB_Contract.ReviewsEntry.TABLE_REVIEWS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case REVIEWS_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TMDB_Contract.ReviewsEntry.TABLE_REVIEWS,
                        projection,
                        TMDB_Contract.ReviewsEntry.COLUMN_REVIEW_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case TRAILERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TMDB_Contract.TrailersEntry.TABLE_TRAILERS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case TRAILERS_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TMDB_Contract.TrailersEntry.TABLE_TRAILERS,
                        projection,
                        TMDB_Contract.TrailersEntry.COLUMN_TRAILER_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default: {
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVOURITE_MOVIES: {
                return TMDB_Contract.FavouriteMoviesEntry.CONTENT_DIR_TYPE;
            }
            case FAVOURITE_MOVIES_WITH_ID: {
                return TMDB_Contract.FavouriteMoviesEntry.CONTENT_ITEM_TYPE;
            }
            case REVIEWS: {
                return TMDB_Contract.ReviewsEntry.CONTENT_DIR_TYPE;
            }
            case REVIEWS_WITH_ID: {
                return TMDB_Contract.ReviewsEntry.CONTENT_ITEM_TYPE;
            }
            case TRAILERS: {
                return TMDB_Contract.TrailersEntry.CONTENT_DIR_TYPE;
            }
            case TRAILERS_WITH_ID: {
                return TMDB_Contract.TrailersEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long _id;
        switch (sUriMatcher.match(uri)) {
            case FAVOURITE_MOVIES: {
                _id = db.insert(TMDB_Contract.FavouriteMoviesEntry.TABLE_FAVOURITE, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = TMDB_Contract.FavouriteMoviesEntry.buildFavouriteMoviesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            case REVIEWS: {
                _id = db.insert(TMDB_Contract.ReviewsEntry.TABLE_REVIEWS, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = TMDB_Contract.ReviewsEntry.buildReviewsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case TRAILERS: {
                _id = db.insert(TMDB_Contract.TrailersEntry.TABLE_TRAILERS, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = TMDB_Contract.TrailersEntry.buildTrailersUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        if (_id > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVOURITE_MOVIES:
                // allows for multiple transactions
                db.beginTransaction();

                // keep track of successful inserts
                int numInserted = 0;
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(TMDB_Contract.FavouriteMoviesEntry.TABLE_FAVOURITE,
                                    null, value);
                        } catch (SQLiteConstraintException e) {
                            Log.w(LOG_TAG, "Attempting to insert " +
                                    value.getAsString(
                                            TMDB_Contract.FavouriteMoviesEntry.COLUMN_MOVIE_ID)
                                    + " but value is already in database.");
                        }
                        if (_id != -1) {
                            numInserted++;
                        }
                    }
                    if (numInserted > 0) {
                        // If no errors, declare a successful transaction.
                        // database will not populate if this is not called
                        db.setTransactionSuccessful();
                    }
                } finally {
                    // all transactions occur at once
                    db.endTransaction();
                }
                if (numInserted > 0) {
                    // if there was successful insertion, notify the content resolver that there
                    // was a change
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case FAVOURITE_MOVIES:
                rowsDeleted = db.delete(TMDB_Contract.FavouriteMoviesEntry.TABLE_FAVOURITE, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(TMDB_Contract.ReviewsEntry.TABLE_REVIEWS, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsDeleted = db.delete(TMDB_Contract.TrailersEntry.TABLE_TRAILERS, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
