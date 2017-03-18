package com.saravanan.popularmovie_1.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.saravanan.popularmovie_1.R;

public class MovieCursorAdapter extends CursorAdapter {

    public static Bitmap convertCursorRowToGridView(Cursor cursor) {
        // get row indices for our cursor
       byte[] myImg = cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieFavourites.MOVIE_POSTER_PATH));

        Bitmap bitmap = BitmapFactory.decodeByteArray(myImg, 0, myImg.length);
        return bitmap;

    }

    public MovieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_exam, viewGroup, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView imgView = (ImageView)view.findViewById(R.id.imgGrid);

        imgView.setImageBitmap(convertCursorRowToGridView(cursor));

    }
}
