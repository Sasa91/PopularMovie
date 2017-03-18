package com.saravanan.popularmovie_1.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.net.URI;

/**
 * Created by DELL on 2/5/2017.
 */

public final class MovieContract {

    public static final String CONTENT_AUTHORITY="com.saravanan.popularmovie_1";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE =  "favourite_movie";

    public static final String PATH_MOVIE_ID =  MovieFavourites.MOVIE_ID_COLUMN;


    Context ctx;

    private MovieContract() {}

    public static final class MovieFavourites implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MovieContract.PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        public static final String FAVOURITES_TABLE_NAME = "favourite_movie";
        public static final String MOVIE_ID_COLUMN = "movie_id";
        public static final String MOVIE_TITLE_COLUMN = "movie_title";
        public static final String MOVIE_POSTER_PATH = "movie_img";
        public static final String MOVIE_RELEASE_DATE = "release_date";
        public static final String MOVIE_RATING = "rating";
        public static final String MOVIE_OVERVIEW = "overview";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static Uri buildMovieWithID(String id){

            return CONTENT_URI.buildUpon().appendPath(MOVIE_ID_COLUMN).appendPath(id).build();

        }

    }

}
