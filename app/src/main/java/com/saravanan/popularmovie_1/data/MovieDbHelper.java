package com.saravanan.popularmovie_1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MovieDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Movies.db";


    Context ctx;

    private static final String DELETE_FAVOURITES_MOVIE =
            "DROP TABLE IF EXISTS " + MovieContract.MovieFavourites.FAVOURITES_TABLE_NAME;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_FAVOURITES_MOVIE =
                "CREATE TABLE " + MovieContract.MovieFavourites.FAVOURITES_TABLE_NAME + " (" +
                        MovieContract.MovieFavourites._ID + " INTEGER PRIMARY KEY," +
                        MovieContract.MovieFavourites.MOVIE_ID_COLUMN + " INTEGER," +
                        MovieContract.MovieFavourites.MOVIE_TITLE_COLUMN + " TEXT," +
                        MovieContract.MovieFavourites.MOVIE_POSTER_PATH + " BLOB," +
                        MovieContract.MovieFavourites.MOVIE_RELEASE_DATE + " TEXT," +
                        MovieContract.MovieFavourites.MOVIE_RATING + " TEXT," +
                        MovieContract.MovieFavourites.MOVIE_OVERVIEW + " TEXT " +
                        ")";

        sqLiteDatabase.execSQL(CREATE_FAVOURITES_MOVIE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieFavourites.FAVOURITES_TABLE_NAME);

    }


    public ContentValues addFavouriteMovieDetails(MovieDetails movie){

        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.MovieFavourites.MOVIE_ID_COLUMN, movie.getMovieID());
        contentValues.put(MovieContract.MovieFavourites.MOVIE_TITLE_COLUMN, movie.getTitle());
        contentValues.put(MovieContract.MovieFavourites.MOVIE_POSTER_PATH, movie.getImgBlob());
        contentValues.put(MovieContract.MovieFavourites.MOVIE_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieFavourites.MOVIE_RATING, movie.getRating());
        contentValues.put(MovieContract.MovieFavourites.MOVIE_OVERVIEW, movie.getOverview());

        return contentValues;

    }


    public List<MovieDetails> getAllFavouriteMovie(Context context){


        SQLiteDatabase db = this.getWritableDatabase();
        List<MovieDetails> movieDetails= new ArrayList<MovieDetails>();

        Cursor cursor =  context.getContentResolver().query(MovieContract.MovieFavourites.CONTENT_URI,
                null,
                null ,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                MovieDetails movie = new MovieDetails();

                movie.setMovieId(Integer.parseInt(cursor.getString(1)));
                movie.setTitle(cursor.getString(2));
                movie.setImgBlob(cursor.getBlob(3));
                movie.setReleaseDate(cursor.getString(4));
                movie.setRating(cursor.getString(5));
                movie.setOverview(cursor.getString(6));
                // Adding contact to list
                movieDetails.add(movie);
            } while (cursor.moveToNext());
        }


        return movieDetails;

    }


}
