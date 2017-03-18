package com.saravanan.popularmovie_1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by DELL on 3/4/2017.
 */

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildURIMatcher();

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;

    MovieDbHelper dbHelper;

    static UriMatcher buildURIMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/"+MovieContract.PATH_MOVIE_ID + "/#", MOVIE_WITH_ID );
       // matcher.addURI(authority, MovieContract.PATH_MOVIE , MOVIE_WITH_ID );

        //Log.d("Matcher","");
        return matcher;

    }


    @Override
    public boolean onCreate()
    {

        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor retCursor;

        switch (sUriMatcher.match(uri)){

            case MOVIE:

                retCursor = db.query(
                        MovieContract.MovieFavourites.FAVOURITES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            case MOVIE_WITH_ID:
               long  _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        MovieContract.MovieFavourites.FAVOURITES_TABLE_NAME,
                        projection,
                        MovieContract.MovieFavourites.MOVIE_ID_COLUMN + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
//            case WEATHER_WITH_LOCATION_AND_DATE:
//            case WEATHER_WITH_LOCATION:
            case MOVIE:
                return MovieContract.MovieFavourites.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieFavourites.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        long _id;
        Uri returnUri;


        switch (sUriMatcher.match(uri)){

            case MOVIE :

                _id = db.insert(MovieContract.MovieFavourites.FAVOURITES_TABLE_NAME, null, contentValues);
                if(_id > 0){
                    returnUri =  MovieContract.MovieFavourites.buildMovieUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }


                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows=0; // Number of rows effected

        switch(sUriMatcher.match(uri)){
            case MOVIE:
                rows = db.delete(MovieContract.MovieFavourites.FAVOURITES_TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
               // rows = db.delete(MovieContract.MovieFavourites.FAVOURITES_TABLE_NAME, "movie_id = ?", new String[] { title });
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because null could delete all rows:
        if(selection == null || rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows;

        switch(sUriMatcher.match(uri)){
            case MOVIE:
                rows = db.update(MovieContract.MovieFavourites.FAVOURITES_TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;

    }
}
