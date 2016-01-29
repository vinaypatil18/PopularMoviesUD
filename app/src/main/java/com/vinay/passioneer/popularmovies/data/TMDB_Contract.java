package com.vinay.passioneer.popularmovies.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TMDB_Contract {

    public static final String CONTENT_AUTHORITY = "com.vinay.passioneer.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVOURITE_MOVIES = "favouriteMovies";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_TRAILERS = "trailers";

    public static final class FavouriteMoviesEntry implements BaseColumns {
        // table name
        public static final String TABLE_FAVOURITE = "favouriteMovies";
        // columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "mID";
        public static final String COLUMN_MOVIE_NAME = "name";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";
        public static final String COLUMN_MOVIE_VOTE_COUNT = "voteCount";
        public static final String COLUMN_MOVIE_VOTE_AVG = "voteAvg";
        public static final String COLUMN_MOVIE_POSTER_IMAGE_PATH = "posterImgPath";
        public static final String COLUMN_MOVIE_BACKDROP_IMAGE_PATH = "backdropImgPath";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_FAVOURITE).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE_MOVIES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE_MOVIES;

        public static Uri buildFavouriteMoviesUri(long movieID){
            return ContentUris.withAppendedId(CONTENT_URI, movieID);
        }
    }

    public static final class ReviewsEntry implements BaseColumns {
        // table name
        public static final String TABLE_REVIEWS = "reviews";
        // columns
        public static final String _ID = "_id";
        public static final String COLUMN_REVIEW_ID = "rID";
        public static final String COLUMN_REVIEW_AUTHOR = "author";
        public static final String COLUMN_REVIEW_CONTENT = "content";
        // Column with the foreign key into the favouriteMovies table.
        public static final String COLUMN_REVIEW_MOVIE_ID = "movieID";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_REVIEWS).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static Uri buildReviewsUri(long movieID){
            return ContentUris.withAppendedId(CONTENT_URI, movieID);
        }
    }

    public static final class TrailersEntry implements BaseColumns {
        // table name
        public static final String TABLE_TRAILERS = "trailers";
        // columns
        public static final String _ID = "_id";
        public static final String COLUMN_TRAILER_ID = "tID";
        public static final String COLUMN_TRAILER_KEY = "key";
        public static final String COLUMN_TRAILER_NAME = "name";
        // Column with the foreign key into the favouriteMovies table.
        public static final String COLUMN_TRAILER_MOVIE_ID = "movieID";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_TRAILERS).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        public static Uri buildTrailersUri(long movieID){
            return ContentUris.withAppendedId(CONTENT_URI, movieID);
        }
    }
}
